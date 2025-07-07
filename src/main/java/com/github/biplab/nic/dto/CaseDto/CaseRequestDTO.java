package com.github.biplab.nic.dto.CaseDto;

import com.github.biplab.nic.dto.CaseDto.CaseDetailsDTO;
import jakarta.validation.constraints.NotBlank;
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
public class CaseRequestDTO {
    @NotBlank(message = "Complainant name is required")
    private String complainantName;

    @NotBlank(message = "Complainant phone is required")
    private String complainantPhone;

    @NotBlank(message = "Case address is required")
    private String caseAddress;

    private String description;

    private LocalDateTime reportedAt;

    @NotBlank(message = "Created by is required")
    private UUID createdBy;

    private String status;

    private CaseDetailsDTO caseDetails; // Optional field for initial case details
}