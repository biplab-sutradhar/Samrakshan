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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        List<Person> policeTeam = teamFormationDTO.getPoliceTeamIds().stream()
                .map(id -> personRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id)))
                .collect(Collectors.toList());

        List<Person> administrativeTeam = teamFormationDTO.getAdministrativeTeamIds().stream()
                .map(id -> personRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id)))
                .collect(Collectors.toList());

        List<Person> diceTeam = teamFormationDTO.getDiceTeamIds().stream()
                .map(id -> personRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Person not found with ID: " + id)))
                .collect(Collectors.toList());

        Person teamLeader = personRepository.findById(teamFormationDTO.getTeamLeaderId())
                .orElseThrow(() -> new RuntimeException("Team leader not found with ID: " + teamFormationDTO.getTeamLeaderId()));

        TeamFormation teamFormation = new TeamFormation();
        teamFormation.setCaseRef(caseRef);
        teamFormation.setPoliceTeam(policeTeam);
        teamFormation.setAdministrativeTeam(administrativeTeam);
        teamFormation.setDiceTeam(diceTeam);
        teamFormation.setTeamLeader(teamLeader);

        TeamFormation savedTeam = teamFormationRepository.save(teamFormation);
        return mapToDTO(savedTeam);
    }

    private TeamFormationDTO mapToDTO(TeamFormation teamFormation) {
        TeamFormationDTO dto = new TeamFormationDTO();
        dto.setCaseId(teamFormation.getCaseRef().getId());
        dto.setPoliceTeamIds(teamFormation.getPoliceTeam().stream().map(Person::getId).collect(Collectors.toList()));
        dto.setAdministrativeTeamIds(teamFormation.getAdministrativeTeam().stream().map(Person::getId).collect(Collectors.toList()));
        dto.setDiceTeamIds(teamFormation.getDiceTeam().stream().map(Person::getId).collect(Collectors.toList()));
        dto.setTeamLeaderId(teamFormation.getTeamLeader().getId());
        return dto;
    }
}