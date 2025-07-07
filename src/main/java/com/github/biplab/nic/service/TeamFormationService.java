package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.TeamFormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        caseRef.setTeamFormation(savedTeam);
        caseRepository.save(caseRef);
        return mapToDTO(savedTeam);
    }

    public TeamFormationDTO getTeamFormationById(UUID id) {
        TeamFormation teamFormation = teamFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + id));
        return mapToDTO(teamFormation);
    }

    public void initiateTeamFormation(UUID caseId) {
        ChildMarriageCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // Select 2-3 persons per department
        List<Person> policeMembers = personRepository.findByDepartmentAndRole(Department.POLICE, null)
                .stream().limit(2).collect(Collectors.toList());
        List<Person> diceMembers = personRepository.findByDepartmentAndRole(Department.DICE, null)
                .stream().limit(2).collect(Collectors.toList());
        List<Person> adminMembers = personRepository.findByDepartmentAndRole(Department.ADMINISTRATION, null)
                .stream().limit(3).collect(Collectors.toList());

        if (policeMembers.isEmpty() || diceMembers.isEmpty() || adminMembers.isEmpty()) {
            throw new RuntimeException("Insufficient members in one or more departments");
        }

        TeamFormationDTO teamFormationDTO = new TeamFormationDTO();
        teamFormationDTO.setCaseId(caseId);
        teamFormationDTO.setPolicePersonId(policeMembers.get(0).getId());
        teamFormationDTO.setDicePersonId(diceMembers.get(0).getId());
        teamFormationDTO.setAdminPersonId(adminMembers.get(0).getId());
        createTeamFormation(teamFormationDTO);

        TeamFormation teamFormation = teamFormationRepository.findByCaseId_Id(caseId)
                .orElseThrow(() -> new RuntimeException("Team formation not found for case ID: " + caseId));
        teamFormation.setPoliceStatus("PENDING");
        teamFormation.setDiceStatus("PENDING");
        teamFormation.setAdminStatus("PENDING");
        teamFormationRepository.save(teamFormation);
    }

    public void handleResponse(UUID teamId, String department, String status) {
        TeamFormation teamFormation = teamFormationRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + teamId));
        switch (department.toUpperCase()) {
            case "POLICE" -> teamFormation.setPoliceStatus(status);
            case "DICE" -> teamFormation.setDiceStatus(status);
            case "ADMINISTRATION" -> teamFormation.setAdminStatus(status);
            default -> throw new RuntimeException("Invalid department");
        }
        teamFormationRepository.save(teamFormation);

        if (isTeamReady(teamFormation) || hasRejections(teamFormation)) {
            handleTeamDecision(teamFormation);
        }
    }

    private boolean isTeamReady(TeamFormation teamFormation) {
        return List.of(teamFormation.getPoliceStatus(), teamFormation.getDiceStatus(), teamFormation.getAdminStatus())
                .stream().allMatch(status -> status.equals("ACCEPTED"));
    }

    private boolean hasRejections(TeamFormation teamFormation) {
        return List.of(teamFormation.getPoliceStatus(), teamFormation.getDiceStatus(), teamFormation.getAdminStatus())
                .stream().anyMatch(status -> status.equals("REJECTED"));
    }

    private void handleTeamDecision(TeamFormation teamFormation) {
        if (isTeamReady(teamFormation)) {
            teamFormation.getCaseId().setStatus("IN_PROGRESS");
            caseRepository.save(teamFormation.getCaseId());
        } else if (hasRejections(teamFormation)) {
            UUID caseId = teamFormation.getCaseId().getId();
            List<Person> supervisors = personRepository.findByRole(Role.SUPERVISOR);
            if (!supervisors.isEmpty()) {
                System.out.println("Escalating case " + caseId + " to supervisor " + supervisors.get(0).getId());
            } else {
                throw new RuntimeException("No supervisors available for escalation");
            }
        }
    }

    private TeamFormationDTO mapToDTO(TeamFormation teamFormation) {
        TeamFormationDTO dto = new TeamFormationDTO();
        dto.setCaseId(teamFormation.getCaseId().getId());
        dto.setPolicePersonId(teamFormation.getPolicePerson().getId());
        dto.setDicePersonId(teamFormation.getDicePerson().getId());
        dto.setAdminPersonId(teamFormation.getAdminPerson().getId());
        dto.setFormedAt(teamFormation.getFormedAt().toString());
        dto.setPoliceStatus(teamFormation.getPoliceStatus());
        dto.setDiceStatus(teamFormation.getDiceStatus());
        dto.setAdminStatus(teamFormation.getAdminStatus());
        return dto;
    }
}