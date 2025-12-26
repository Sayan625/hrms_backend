package com.hrms.sol.controller;

import com.hrms.sol.entity.Employee;
import com.hrms.sol.repository.EmployeeRepository;
import com.hrms.sol.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AuthController {

    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(EmployeeRepository employeeRepo,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.employeeRepo = employeeRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Employee emp) {

        Employee dbEmp = employeeRepo.findByEmail(emp.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(emp.getPassword(), dbEmp.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(dbEmp.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("id", dbEmp.getId().toString());
        response.put("name", dbEmp.getName());
        response.put("email", dbEmp.getEmail());
        response.put("type", dbEmp.getType());
        response.put("phone", dbEmp.getPhone());
        response.put("hireDate", dbEmp.getHireDate().toString());
        response.put("department", dbEmp.getDepartment());
        response.put("position", dbEmp.getPosition());

        return response;
    }

}
