package com.github.biplab.nic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "child_marriage_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildMarriageCase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "complainant_name", nullable = false)
    private String complainantName;

    @Column(name = "complainant_phone", nullable = false)
    private String complainantPhone;

    @Column(name = "case_address", nullable = false)
    private String caseAddress;

    @Column(name = "description")
    private String description;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Person createdBy;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "caseId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaseDetails> caseDetails = new ArrayList<>(); // Initialize as empty list

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (reportedAt == null) reportedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}