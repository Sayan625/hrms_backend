package com.hrms.sol.repository;

import com.hrms.sol.entity.Department;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("""
        SELECT d.id, d.name, COUNT(e.id)
        FROM Department d
        LEFT JOIN Employee e
            ON e.department = d.name
        GROUP BY d.id, d.name
    """)
    List<Object[]> getDepartmentEmployeeCount();
}
