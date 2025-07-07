package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.TeamFormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TeamFormationService {

    @Autowired
    private TeamFormationRepository teamFormationRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private PersonRepository personRepository;

    public TeamFormationDTO createTeamFormation(TeamFormationDTO teamFormationDTO) {
        ChildMarriageCase caseRef = caseRepository.findById(teamFormationDTO.getCaseId())
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + teamFormationDTO.getCaseId()));

        Person policePerson = personRepository.findById(teamFormationDTO.getPolicePersonId())
                .orElseThrow(() -> new RuntimeException("Police person not found with ID: " + teamFormationDTO.getPolicePersonId()));

        Person dicePerson = personRepository.findById(teamFormationDTO.getDicePersonId())
                .orElseThrow(() -> new RuntimeException("DICE person not found with ID: " + teamFormationDTO.getDicePersonId()));

        Person adminPerson = personRepository.findById(teamFormationDTO.getAdminPersonId())
                .orElseThrow(() -> new RuntimeException("Admin person not found with ID: " + teamFormationDTO.getAdminPersonId()));

        TeamFormation teamFormation = new TeamFormation();
        teamFormation.setCaseId(caseRef);
        teamFormation.setPolicePerson(policePerson);
        teamFormation.setDicePerson(dicePerson);
        teamFormation.setAdminPerson(adminPerson);
        teamFormation.setFormedAt(LocalDateTime.now());

        TeamFormation savedTeam = teamFormationRepository.save(teamFormation);
        return mapToDTO(savedTeam);
    }

    public TeamFormationDTO getTeamFormationById(UUID id) {
        TeamFormation teamFormation = teamFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + id));
        return mapToDTO(teamFormation);
    }

    private TeamFormationDTO mapToDTO(TeamFormation teamFormation) {
        TeamFormationDTO dto = new TeamFormationDTO();
        dto.setCaseId(teamFormation.getCaseId().getId());
        dto.setPolicePersonId(teamFormation.getPolicePerson().getId());
        dto.setDicePersonId(teamFormation.getDicePerson().getId());
        dto.setAdminPersonId(teamFormation.getAdminPerson().getId());
        dto.setFormedAt(teamFormation.getFormedAt().toString()); // Convert to String for now
        return dto;
    }
}