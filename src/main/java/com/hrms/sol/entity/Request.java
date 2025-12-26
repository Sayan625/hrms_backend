package com.hrms.sol.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "seq", allocationSize = 1)
    private Long id;

    private Long userId;

    private String type;

    private String status;

    public String reason;

    @Column(columnDefinition = "text")
    private String payload; 

    private String remark;

}
