package com.github.biplab.nic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "case_details_department_members", joinColumns = @JoinColumn(name = "case_details_id"))
    @MapKeyColumn(name = "department")
    @Column(name = "member_id")
    private Map<String, List<UUID>> departmentMembers = new HashMap<>();

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

    @Column(name = "boy_subdivision")
    private String boySubdivision;

    @Column(name = "girl_name")
    private String girlName;

    @Column(name = "girl_father_name")
    private String girlFatherName;

    @Column(name = "girl_address")
    private String girlAddress;

    @Column(name = "girl_subdivision")
    private String girlSubdivision;

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "marriage_address")
    private String marriageAddress;

    @Column(name = "marriage_location_landmark")
    private String marriagelocationlandmark;

    @Column(name = "police_station_near_marriage_location")
    private String policeStationNearMarriageLocation;

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
