package com.hrms.sol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDate date;
    private double status;
    private String type;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String remarks;

}