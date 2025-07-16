package com.github.biplab.nic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @Column(name = "team_id", nullable = false, updatable = false)
    private UUID teamId;

    @OneToOne
    @JoinColumn(name = "case_id", nullable = false)
    private ChildMarriageCase caseId;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Person supervisor;

    @ElementCollection
    @CollectionTable(name = "team_formation_member_ids", joinColumns = @JoinColumn(name = "team_formation_team_id"))
    @Column(name = "member_id", nullable = false)
    private List<UUID> memberIds;

    @Column(name = "formed_at", nullable = true)
    private LocalDateTime formedAt;

    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;

    @Column(name = "police_status")
    private String policeStatus;

    @Column(name = "dice_status")
    private String diceStatus;

    @Column(name = "admin_status")
    private String adminStatus;

    @Column(name = "response_status") // You can choose a different column name if needed
    private String response;


    @PrePersist
    protected void onCreate() {

        if (this.formedAt == null) {
            this.formedAt = LocalDateTime.now();
        }
    }

    // Rename getId to getTeamId for consistency
    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }
}