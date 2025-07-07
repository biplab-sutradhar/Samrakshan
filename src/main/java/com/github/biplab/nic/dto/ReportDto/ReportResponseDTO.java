package com.github.biplab.nic.dto.ReportDto;

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
public class ReportResponseDTO {
    private UUID id;
    private UUID caseId;
    private UUID submittedBy;
    private String reportDetails;
    private LocalDateTime submissionDate;
}