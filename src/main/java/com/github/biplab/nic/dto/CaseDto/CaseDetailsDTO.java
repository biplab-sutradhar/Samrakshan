package com.github.biplab.nic.dto.CaseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
}