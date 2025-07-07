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

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private ChildMarriageCase caseId;

    @ManyToOne
    @JoinColumn(name = "police_person", nullable = false)
    private Person policePerson;

    @ManyToOne
    @JoinColumn(name = "dice_person", nullable = false)
    private Person dicePerson;

    @ManyToOne
    @JoinColumn(name = "admin_person", nullable = false)
    private Person adminPerson;

    @Column(name = "formed_at")
    private LocalDateTime formedAt;

    @PrePersist
    protected void onCreate() {
        formedAt = LocalDateTime.now();
    }
}