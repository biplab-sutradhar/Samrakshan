package com.github.biplab.nic.dto.CaseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseRequestDTO {
    private String complainantName;
    private String complainantPhone;
    private String caseAddress;
    private String district;
    private String state;
    private String description;
    private LocalDateTime reportedAt;
    private String createdBy;
    private String status;
    private CaseDetailsDTO caseDetails;
}