package com.github.biplab.nic.dto;

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
public class TeamFormationDTO {
    @NotNull(message = "Case ID is required")
    private UUID caseId;

    @NotNull(message = "Police person ID is required")
    private UUID policePersonId;

    @NotNull(message = "DICE person ID is required")
    private UUID dicePersonId;

    @NotNull(message = "Admin person ID is required")
    private UUID adminPersonId;

    @NotNull(message = "Formed at is required")
    private String formedAt; // Placeholder; use LocalDateTime in practice
}