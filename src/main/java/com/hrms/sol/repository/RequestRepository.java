package com.hrms.sol.repository;

import com.hrms.sol.entity.Request;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
               SELECT r, e.name
               FROM Request r
               JOIN Employee e ON r.userId = e.id
               WHERE r.status = :status
            """)
    List<Object[]> findByStatus(@Param("status") String status);

    List<Request> findByUserId(Long userId);
}
