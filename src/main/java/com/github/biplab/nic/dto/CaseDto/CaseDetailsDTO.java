package com.github.biplab.nic.dto.CaseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetailsDTO {
    private UUID id;
    private UUID caseId;
    private String notes;
    private String evidencePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UUID> policeMembers;
    private List<UUID> diceMembers;
    private List<UUID> adminMembers;
    private UUID supervisorId;
    private LocalDateTime marriageDate;
    private String boyName;
    private String boyFatherName;
    private String boyAddress;
    private Integer boyAge;
    private String girlName;
    private String girlFatherName;
    private Integer girlAge;
    private String girlAddress;
    private String girlVillage;
    private String girlPoliceStation;
    private String girlPostOffice;
    private String girlSubdivision;
    private String girlDistrict;
    private UUID teamId;
    private String marriageAddress;
}