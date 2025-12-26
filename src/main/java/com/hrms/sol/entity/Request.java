package com.hrms.sol.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String type;

    private String status;

    public String reason;

    @Column(columnDefinition = "TEXT")
    private String payload; 

    private String remark;

}
