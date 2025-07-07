package com.github.biplab.nic.dto.CaseDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseRequestDTO {
    @NotBlank(message = "Complainant is required")
    private String complainant;

    @NotBlank(message = "Address is required")
    private String address;

    private String caseDetails;

    private LocalDate marriageDate;
}