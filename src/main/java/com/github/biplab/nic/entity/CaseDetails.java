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
@Table(name = "case_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private ChildMarriageCase caseId;

    @Column(name = "notes", length = 10000)
    private String notes;

    @Column(name = "evidence_path")
    private String evidencePath;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @Column(name = "police_members")
    private List<UUID> policeMembers = new ArrayList<>();

    @ElementCollection
    @Column(name = "dice_members")
    private List<UUID> diceMembers = new ArrayList<>();

    @ElementCollection
    @Column(name = "admin_members")
    private List<UUID> adminMembers = new ArrayList<>();

    @Column(name = "supervisor_id")
    private UUID supervisorId;

    @Column(name = "marriage_date")
    private LocalDateTime marriageDate;

    @Column(name = "boy_name")
    private String boyName;

    @Column(name = "boy_father_name")
    private String boyFatherName;

    @Column(name = "boy_address")
    private String boyAddress;

    @Column(name = "boy_age")
    private Integer boyAge;

    @Column(name = "girl_name")
    private String girlName;

    @Column(name = "girl_father_name")
    private String girlFatherName;

    @Column(name = "girl_age")
    private Integer girlAge;

    @Column(name = "girl_address")
    private String girlAddress;

    @Column(name = "girl_village")
    private String girlVillage;

    @Column(name = "girl_police_station")
    private String girlPoliceStation;

    @Column(name = "girl_post_office")
    private String girlPostOffice;

    @Column(name = "girl_subdivision")
    private String girlSubdivision;

    @Column(name = "girl_district")
    private String girlDistrict;

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "marriage_address")
    private String marriageAddress;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
