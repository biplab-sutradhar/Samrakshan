package com.github.biplab.nic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamFormationDTO {
    private UUID caseId;

    private UUID policePersonId;

    private UUID dicePersonId;

    private UUID adminPersonId;

    private String formedAt;

    private String policeStatus;

    private String diceStatus;

    private String adminStatus;
}