package com.github.biplab.nic.dto.CaseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetailsDTO {
    private UUID id;
    private UUID caseId;
    private String evidencePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, List<UUID>> departmentMembers;  // New: dynamic map
    private UUID supervisorId;
    private LocalDateTime marriageDate;
    private String boyName;
    private String boyFatherName;
    private String girlName;
    private String girlFatherName;
    private String girlAddress;
    private String girlSubdivision;
    private UUID teamId;
    private String marriageAddress;
    private String policeStationNearMarriageLocation;
    // Remove fixed lists like policeMembers, diceMembers, adminMembers
}
