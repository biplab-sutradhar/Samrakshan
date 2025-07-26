package com.github.biplab.nic.dto.ReportDto;

import com.github.biplab.nic.enums.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {

        @NotNull
        private UUID caseId;

        @NotNull
        private UUID personId;

        @NotBlank
        private String report;

        @NotBlank
        private String department;
    }
