package com.hrms.sol.controller;

import com.hrms.sol.entity.Department;
import com.hrms.sol.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin/departments")
@CrossOrigin
public class DepartmentController {

    private final DepartmentRepository repo;

    public DepartmentController(DepartmentRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Department create(@RequestBody Department d) {
        return repo.save(d);
    }

    @GetMapping
    public List<Department> getAll() {
        return repo.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }

    @GetMapping("/with-count")
    public List<Map<String, Object>> getDepartmentEmployeeCount() {
        List<Object[]> rows = repo.getDepartmentEmployeeCount();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("department", row[1]);
            map.put("count", row[2]);
            result.add(map);
        }

        return result;
    }
}
