package com.github.biplab.nic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamFormationDTO {
    @NotNull(message = "Case ID is required")
    private UUID caseId;

    @NotNull(message = "Police team IDs are required")
    private List<UUID> policeTeamIds;

    @NotNull(message = "Administrative team IDs are required")
    private List<UUID> administrativeTeamIds;

    @NotNull(message = "DICE team IDs are required")
    private List<UUID> diceTeamIds;

    @NotNull(message = "Team leader ID is required")
    private UUID teamLeaderId;
}