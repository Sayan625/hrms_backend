package com.hrms.sol.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "seq", allocationSize = 1)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private LocalDate hireDate;
    private LocalDate exitDate;
    private String department;
    private String position;
    private String type;
}