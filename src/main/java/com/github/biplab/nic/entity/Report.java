package com.github.biplab.nic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "person_id")
    private UUID personId; // Changed from Person to UUID

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    private String department; // New: For uniqueness constraint

    @Column(name = "final_content")
    private String finalContent; // New: JSON string for merged report

    @Column(name = "is_merged")
    private Boolean isMerged = false;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}