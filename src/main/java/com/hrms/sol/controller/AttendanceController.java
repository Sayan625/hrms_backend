package com.hrms.sol.controller;

import com.hrms.sol.entity.Attendance;
import com.hrms.sol.repository.AttendanceRepository;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AttendanceController {

    private final AttendanceRepository repo;

    public AttendanceController(AttendanceRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/user/checkin/{id}")
    public Attendance checkIn(@PathVariable Long id) {
        LocalDate today = LocalDate.now();

        Attendance attendance = repo.findByUserIdAndDate(id, today)
                .orElse(new Attendance(null, id, today, 0.5, "normal", null, null, null));

        attendance.setCheckIn(LocalTime.now());

        return repo.save(attendance);
    }

    @PostMapping("/user/checkout/{id}")
    public Attendance checkOut(@PathVariable Long id) {
        Attendance attendance = repo.findByUserIdAndDate(id, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        attendance.setCheckOut(LocalTime.now());
        LocalTime checkoutTime = LocalTime.now();
        attendance.setCheckOut(checkoutTime);

        long minutesWorked = java.time.Duration.between(attendance.getCheckIn(), checkoutTime).toMinutes();
        double hoursWorked = minutesWorked / 60.0;

        if (hoursWorked > 4) {
            attendance.setStatus(1.0);
        } else {
            attendance.setStatus(0.5);
        }
        return repo.save(attendance);
    }

    @GetMapping("/admin/attendance/{date}")
    public List<Map<String, Object>> getTodayAttendanceForHR(@PathVariable String date) {

        LocalDate selectedDate = LocalDate.parse(date);
        List<Object[]> rows = repo.getTodayAttendance(selectedDate);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();

            Long empId = (Long) row[0];
            String name = (String) row[1];
            Double status = (Double) row[2];
            LocalTime checkIn = (LocalTime) row[3];
            LocalTime checkOut = (LocalTime) row[4];
            String type = (String) row[5];
            map.put("userId", empId);
            map.put("name", name);

            if (checkIn != null) {
                map.put("status", "PRESENT");
                map.put("checkIn", checkIn);
                map.put("checkOut", checkOut);
            } else if (type != null && type.equalsIgnoreCase("leave")) {
                map.put("status", "LEAVE");
                map.put("checkIn", null);
                map.put("checkOut", null);
            } else {
                map.put("status", "ABSENT");
                map.put("checkIn", null);
                map.put("checkOut", null);
            }

            result.add(map);
        }

        return result;
    }

    @GetMapping("/admin/attendance/user")
    public Map<String, Object> getEmployeeAttendance(
            @RequestParam Long user,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);

        Attendance a = repo.findByUserIdAndDate(user, localDate)
                .orElse(null);

        Map<String, Object> res = new HashMap<>();
        res.put("userId", user);

        if (a == null) {
            res.put("status", 0);
            res.put("checkIn", null);
            res.put("checkOut", null);
        } else {
            res.put("attendanceId", a.getId());
            res.put("status", a.getStatus());
            res.put("checkIn", a.getCheckIn());
            res.put("checkOut", a.getCheckOut());
        }

        return res;
    }

    @PutMapping("/admin/attendance")
    public Attendance upsertAttendance(@RequestBody Map<String, String> req) {

        Long employeeId = Long.parseLong(req.get("userId"));
        LocalDate date = LocalDate.parse(req.get("date"));
        LocalTime checkIn = null;
        LocalTime checkOut = null;

        if (req.get("checkIn") != null && !req.get("checkIn").isBlank()) {
            checkIn = LocalTime.parse(req.get("checkIn"));
        }

        if (req.get("checkOut") != null && !req.get("checkOut").isBlank()) {
            checkOut = LocalTime.parse(req.get("checkOut"));
        }

        Attendance attendance = repo.findByUserIdAndDate(employeeId, date)
                .orElse(new Attendance(null, employeeId, date, 0.5, req.get("type"), null, null, null));

        attendance.setCheckIn(checkIn);
        attendance.setCheckOut(checkOut);
        if (checkIn != null && checkOut != null) {

            long minutesWorked = Duration.between(checkIn, checkOut).toMinutes();

            double hoursWorked = minutesWorked / 60.0;

            attendance.setStatus(hoursWorked > 7 ? 1.0 : 0.5);

        } else {
            attendance.setStatus(1.0);

        }

        return repo.save(attendance);
    }

    @GetMapping("/user/attendance/monthly")
    public List<Map<String, Object>> getMonthlyAttendance(
            @RequestParam Long user,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();

        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();

        List<Attendance> list = repo.findMonthlyAttendance(user, m, y);

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Attendance a : list) {
            if (a.getDate().isAfter(today)) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("date", a.getDate());
            map.put("status", a.getStatus());
            map.put("checkIn", a.getCheckIn());
            map.put("checkOut", a.getCheckOut());
            map.put("type", a.getType());
            result.add(map);
        }

        return result;
    }

    @GetMapping("/user/attendance/monthly-summary")
    public Map<String, Object> getMonthlySummary(
            @RequestParam Long user,
            @RequestParam int month,
            @RequestParam int year) {

        Object[] rowWrapper = repo.getMonthlySummary(user, month, year);
        Object[] row = (Object[]) rowWrapper[0];

        System.out.println("Monthly summary row: " + Arrays.toString(row));
        long fullDays = row[0] == null ? 0 : ((Number) row[0]).longValue();
        long halfDays = row[1] == null ? 0 : ((Number) row[1]).longValue();
        double totalWork = row[2] == null ? 0.0 : ((Number) row[2]).doubleValue();

        Map<String, Object> res = new HashMap<>();
        res.put("userId", user);
        res.put("month", month);
        res.put("year", year);
        res.put("fullDays", fullDays);
        res.put("halfDays", halfDays);
        res.put("totalUnits", totalWork);

        return res;
    }

    @PostMapping("/admin/attendance/leave")
    public List<Attendance> applyLeave(@RequestBody Map<String, String> req) {

        Long userId = Long.parseLong(req.get("userId"));
        LocalDate fromDate = LocalDate.parse(req.get("fromDate"));
        LocalDate toDate = LocalDate.parse(req.get("toDate"));
        double status=Double.parseDouble(req.get("status"));

        if (fromDate.isAfter(toDate)) {
            throw new RuntimeException("From date cannot be after To date");
        }

        List<Attendance> saved = new ArrayList<>();

        LocalDate date = fromDate;
        while (!date.isAfter(toDate)) {

            Attendance attendance = repo.findByUserIdAndDate(userId, date)
                    .orElse(new Attendance(
                            null,
                            userId,
                            date,
                            status,
                            "leave",
                            null,
                            null,
                            null));

            attendance.setType("leave");
            attendance.setCheckIn(null);
            attendance.setCheckOut(null);

            saved.add(repo.save(attendance));

            date = date.plusDays(1);
        }

        return saved;
    }

}