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
public class CaseResponseDTO {
    private UUID id;
    private String complainantName;
    private String complainantPhone;
    private String caseAddress;
    private String district;
    private String state;
    private String description;
    private LocalDateTime reportedAt;
    private String createdBy;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CaseDetailsDTO> caseDetails;
}