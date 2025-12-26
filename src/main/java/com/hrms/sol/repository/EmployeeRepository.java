package com.hrms.sol.repository;

import com.hrms.sol.entity.Employee;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;

import java.util.*;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);
}

