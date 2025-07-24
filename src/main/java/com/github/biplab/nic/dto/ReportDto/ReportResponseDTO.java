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
    private Long id;
    private UUID caseId;
    private UUID personId;
    private String content;
    private LocalDateTime submittedAt;
    private String department;
    private String finalContent;
    private Boolean isMerged;

}