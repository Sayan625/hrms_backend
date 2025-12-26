package com.hrms.sol.repository;
import com.hrms.sol.entity.Attendance;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;


public interface AttendanceRepository extends JpaRepository<Attendance, Long> {


    Optional<Attendance> findByUserIdAndDate(Long employeeId, LocalDate date);

    @Query("""
        SELECT e.id, e.name, a.status, a.checkIn, a.checkOut, a.type
        FROM Employee e
        LEFT JOIN Attendance a
        ON e.id = a.userId
        AND a.date = :today
    """)
    List<Object[]> getTodayAttendance(@Param("today") LocalDate today);

    @Query("""
    FROM Attendance a
    WHERE a.userId = :userId
      AND MONTH(a.date) = :month
      AND YEAR(a.date) = :year
    ORDER BY a.date DESC
""")
    List<Attendance> findMonthlyAttendance(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
    SELECT 
        SUM(CASE WHEN a.status = 1 THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.status = 0.5 THEN 1 ELSE 0 END),
        SUM(a.status)
    FROM Attendance a
    WHERE a.userId = :userId
      AND MONTH(a.date) = :month
      AND YEAR(a.date) = :year
""")
    Object[] getMonthlySummary(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

}
