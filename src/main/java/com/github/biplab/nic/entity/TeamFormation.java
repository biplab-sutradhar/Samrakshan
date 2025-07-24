package com.github.biplab.nic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "team_formation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID teamId;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private ChildMarriageCase caseId;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Person supervisor;

    @ElementCollection
    @Column(name = "member_ids")
    private List<UUID> memberIds = new ArrayList<>();  // Flat list of all member IDs for compatibility

    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;

    @Column(name = "formed_at")
    private LocalDateTime formedAt;

    // Dynamic department statuses (department name -> status like "PENDING", "ACCEPTED", "REJECTED", "NO_MEMBERS")
    @ElementCollection
    @CollectionTable(name = "team_formation_department_statuses", joinColumns = @JoinColumn(name = "team_formation_id"))
    @MapKeyColumn(name = "department")
    @Column(name = "status")
    private Map<String, String> departmentStatuses = new HashMap<>();

    @Column(name = "locked")
    private boolean locked = false;

    @ElementCollection
    @CollectionTable(name = "team_formation_department_members", joinColumns = @JoinColumn(name = "team_formation_id"))
    @MapKeyColumn(name = "department")
    private Map<String, List<UUID>> departmentMembers = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        notificationSentAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        // No specific update logic needed, but can add if required
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean getLocked() {
        return locked;
    }
}
