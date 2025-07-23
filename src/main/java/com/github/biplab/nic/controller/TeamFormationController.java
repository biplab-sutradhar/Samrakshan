package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.service.TeamFormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/team-formations")
public class TeamFormationController {

    @Autowired
    private TeamFormationService teamFormationService;

    @GetMapping("/{id}")
    public ResponseEntity<TeamFormationDTO> getTeamFormationById(@PathVariable UUID id) {
        TeamFormationDTO dto = teamFormationService.getTeamFormationById(id);
        return ResponseEntity.ok(dto);  // Now returns dynamic maps
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<TeamFormationDTO> getTeamFormationByCaseId(@PathVariable UUID caseId) {
        TeamFormationDTO dto = teamFormationService.getTeamFormationByCaseId(caseId);
        return ResponseEntity.ok(dto);
    }
}