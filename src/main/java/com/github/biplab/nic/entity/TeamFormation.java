package com.github.biplab.nic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "team_formation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "case_id", nullable = false)
    private ChildMarriageCase caseId;

    @ManyToOne
    @JoinColumn(name = "police_person_id", nullable = false)
    private Person policePerson;

    @ManyToOne
    @JoinColumn(name = "dice_person_id", nullable = false)
    private Person dicePerson;

    @ManyToOne
    @JoinColumn(name = "admin_person_id", nullable = false)
    private Person adminPerson;

    @Column(name = "formed_at")
    private LocalDateTime formedAt;

    @Column(name = "police_status")
    private String policeStatus; // e.g., "PENDING", "ACCEPTED", "REJECTED"

    @Column(name = "dice_status")
    private String diceStatus;

    @Column(name = "admin_status")
    private String adminStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (formedAt == null) formedAt = LocalDateTime.now();
        if (policeStatus == null) policeStatus = "PENDING";
        if (diceStatus == null) diceStatus = "PENDING";
        if (adminStatus == null) adminStatus = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}