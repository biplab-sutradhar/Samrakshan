package com.github.biplab.nic.dto.ReportDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {
    @NotNull(message = "Case ID is required")
    private UUID caseId;

    @NotNull(message = "Submitted by ID is required")
    private UUID submittedBy;

    @NotBlank(message = "Report details are required")
    private String reportDetails;
}