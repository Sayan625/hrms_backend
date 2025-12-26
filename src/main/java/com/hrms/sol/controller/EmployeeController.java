package com.hrms.sol.controller;

import com.hrms.sol.entity.Employee;
import com.hrms.sol.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.hrms.sol.security.JwtUtil;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class EmployeeController {
    private final EmployeeRepository repo;
    private final PasswordEncoder passwordEncoder;
    public EmployeeController(EmployeeRepository repo, PasswordEncoder passwordEncoder)
    {   this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/register")
    public String create(@RequestBody Employee e) {
        if (repo.findByEmail(e.getEmail()).isPresent()) {
            return "Employee exist";
        }

        e.setPassword(passwordEncoder.encode(e.getPassword()));
        repo.save(e);
        return "Employee registered successfully";

    }

    @GetMapping("/admin/users")
    public List<Employee> getAll() { return repo.findAll(); }

    @GetMapping("/user/{id}")
    public Employee get(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PutMapping("/user/{id}")
    public Employee update(@PathVariable Long id, @RequestBody Employee updated) {

        Employee emp = repo.findById(id).orElse(null);
        if (emp == null) return null;

        if (updated.getName() != null) emp.setName(updated.getName());
        if (updated.getEmail() != null) emp.setEmail(updated.getEmail());
        if (updated.getPhone() != null) emp.setPhone(updated.getPhone());
        if (updated.getHireDate() != null) emp.setHireDate(updated.getHireDate());
        if (updated.getExitDate() != null) emp.setExitDate(updated.getExitDate());
        if (updated.getDepartment() != null) emp.setDepartment(updated.getDepartment());
        if (updated.getPosition() != null) emp.setPosition(updated.getPosition());
        if (updated.getType() != null) emp.setType(updated.getType());

        return repo.save(emp);
    }


    @DeleteMapping("/admin/user/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}