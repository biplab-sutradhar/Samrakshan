package com.github.biplab.nic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Column(name = "person_id")
    private UUID personId;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "department")
    private String department;

    @Column(name = "report", nullable = false, columnDefinition = "TEXT")
    private String report;

    @Column(name = "is_final_report", nullable = false)
    private Boolean isFinalReport = false; // Fixed typo

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
