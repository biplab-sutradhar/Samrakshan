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
@Table(name = "team_formation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private ChildMarriageCase caseId;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Person supervisor;

    @ElementCollection
    @Column(name = "member_ids")
    private List<UUID> memberIds = new ArrayList<>();

    @Column(name = "formed_at", nullable = false)
    private LocalDateTime formedAt;

    @Column(name = "police_status")
    private String policeStatus;

    @Column(name = "dice_status")
    private String diceStatus;

    @Column(name = "admin_status")
    private String adminStatus;

    @PrePersist
    protected void onCreate() {
        formedAt = LocalDateTime.now();
    }
}