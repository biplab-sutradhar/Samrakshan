package com.github.biplab.nic.dto.ReportDto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {

        private Long id;
        private UUID caseId;
        private UUID personId;
        private String report;
        private String department;
        private LocalDateTime submittedAt;
        private Boolean isFinalReport;
    }