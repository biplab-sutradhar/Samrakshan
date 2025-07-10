package com.github.biplab.nic.dto.ReportDto;

import com.github.biplab.nic.enums.Department;
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

    @NotNull(message = "Person ID is required")
    private UUID personId;

    @NotNull(message = "Submitted by ID is required")
    private UUID submittedBy;



    @NotNull(message = "Department is required")
    private Department department;

    @NotBlank(message = "Content is required")
    private String content;
}