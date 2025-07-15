package com.github.biplab.nic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamFormationDTO {
    private UUID caseId;
    private UUID supervisorId;
    private List<UUID> memberIds;
    private LocalDateTime formedAt;
    private String policeStatus;
    private String diceStatus;
    private String adminStatus;
}