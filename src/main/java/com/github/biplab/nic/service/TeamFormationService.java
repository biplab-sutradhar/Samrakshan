package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.CaseDetails;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role;
import com.github.biplab.nic.repository.CaseDetailsRepository;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.TeamFormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private CaseDetailsRepository caseDetailsRepository;

    public TeamFormationDTO createTeamFormation(TeamFormationDTO teamFormationDTO) {
        ChildMarriageCase caseRef = caseRepository.findById(teamFormationDTO.getCaseId())
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + teamFormationDTO.getCaseId()));
        Person supervisor = personRepository.findById(teamFormationDTO.getSupervisorId())
                .orElseThrow(() -> new RuntimeException("Supervisor not found with ID: " + teamFormationDTO.getSupervisorId()));
        if (!supervisor.getRole().equals(Role.SUPERVISOR)) {
            throw new RuntimeException("Selected supervisor must have SUPERVISOR role");
        }

        TeamFormation teamFormation = new TeamFormation();
        teamFormation.setCaseId(caseRef);
        teamFormation.setSupervisor(supervisor);
        teamFormation.setMemberIds(teamFormationDTO.getMemberIds());
        teamFormation.setFormedAt(LocalDateTime.now());
        TeamFormation savedTeam = teamFormationRepository.save(teamFormation);
        caseRef.setTeamFormation(savedTeam);
        caseRepository.save(caseRef);

        // Update CaseDetails with teamId
        CaseDetails caseDetails = caseRef.getCaseDetails().isEmpty() ? new CaseDetails() : caseRef.getCaseDetails().get(0);
        caseDetails.setCaseId(caseRef);
        caseDetails.setPoliceMembers(new ArrayList<>());
        caseDetails.setDiceMembers(new ArrayList<>());
        caseDetails.setAdminMembers(new ArrayList<>());
        caseDetails.setSupervisorId(supervisor.getId());
        caseDetails.setTeamId(savedTeam.getId()); // Set teamId
        caseDetailsRepository.save(caseDetails);
        caseRef.getCaseDetails().clear();
        caseRef.getCaseDetails().add(caseDetails);

        return mapToDTO(savedTeam);
    }

    public TeamFormationDTO getTeamFormationById(UUID id) {
        TeamFormation teamFormation = teamFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + id));
        return mapToDTO(teamFormation);
    }

    public void initiateTeamFormation(UUID caseId, String district) {
        ChildMarriageCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // Select supervisor
        List<Person> supervisors = personRepository.findByRoleAndDistrict(Role.SUPERVISOR, district);
        if (supervisors.isEmpty()) {
            throw new RuntimeException("No supervisor available in district: " + district);
        }
        Person supervisor = supervisors.get(0); // Take the first available supervisor

        // Select members (2 from each department)
        List<Person> policeMembers = personRepository.findByDepartmentAndDistrictAndRole(Department.POLICE, district, Role.MEMBER)
                .stream().limit(2).collect(Collectors.toList());
        List<Person> diceMembers = personRepository.findByDepartmentAndDistrictAndRole(Department.DICE, district, Role.MEMBER)
                .stream().limit(2).collect(Collectors.toList());
        List<Person> adminMembers = personRepository.findByDepartmentAndDistrictAndRole(Department.ADMINISTRATION, district, Role.MEMBER)
                .stream().limit(2).collect(Collectors.toList());

        // Ensure at least 6 members (2 per department)
        if (policeMembers.size() < 2 || diceMembers.size() < 2 || adminMembers.size() < 2) {
            throw new RuntimeException("Insufficient members available in district: " + district);
        }

        // Create TeamFormation
        TeamFormation teamFormation = new TeamFormation();
        teamFormation.setCaseId(caseEntity);
        teamFormation.setSupervisor(supervisor);
        List<UUID> allMemberIds = new ArrayList<>();
        allMemberIds.addAll(policeMembers.stream().map(Person::getId).collect(Collectors.toList()));
        allMemberIds.addAll(diceMembers.stream().map(Person::getId).collect(Collectors.toList()));
        allMemberIds.addAll(adminMembers.stream().map(Person::getId).collect(Collectors.toList()));
        teamFormation.setMemberIds(allMemberIds);
        teamFormation.setFormedAt(LocalDateTime.now());
        TeamFormation savedTeam = teamFormationRepository.save(teamFormation);
        caseEntity.setTeamFormation(savedTeam);
        caseRepository.save(caseEntity);

        // Update CaseDetails with members
        CaseDetails caseDetails = caseEntity.getCaseDetails().isEmpty() ? new CaseDetails() : caseEntity.getCaseDetails().get(0);
        caseDetails.setCaseId(caseEntity);
        caseDetails.setPoliceMembers(policeMembers.stream().map(Person::getId).collect(Collectors.toList()));
        caseDetails.setDiceMembers(diceMembers.stream().map(Person::getId).collect(Collectors.toList()));
        caseDetails.setAdminMembers(adminMembers.stream().map(Person::getId).collect(Collectors.toList()));
        caseDetails.setSupervisorId(supervisor.getId());
        caseDetails.setTeamId(savedTeam.getId());
        caseDetailsRepository.save(caseDetails);
        if (caseEntity.getCaseDetails().isEmpty()) {
            caseEntity.getCaseDetails().add(caseDetails);
        }
        caseRepository.save(caseEntity);
    }

    public void handleResponse(UUID teamId, UUID personId, String department, String status) {
        TeamFormation teamFormation = teamFormationRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + teamId));
        ChildMarriageCase caseEntity = teamFormation.getCaseId();
        CaseDetails caseDetails = caseEntity.getCaseDetails().get(0);

        switch (department.toUpperCase()) {
            case "POLICE" -> {
                teamFormation.setPoliceStatus(status);
                if ("ACCEPTED".equals(status)) {
                    caseDetails.getPoliceMembers().add(personId);
                } else if ("REJECTED".equals(status)) {
                    replaceMember(teamId, Department.POLICE, personId);
                }
            }
            case "DICE" -> {
                teamFormation.setDiceStatus(status);
                if ("ACCEPTED".equals(status)) {
                    caseDetails.getDiceMembers().add(personId);
                } else if ("REJECTED".equals(status)) {
                    replaceMember(teamId, Department.DICE, personId);
                }
            }
            case "ADMINISTRATION" -> {
                teamFormation.setAdminStatus(status);
                if ("ACCEPTED".equals(status)) {
                    caseDetails.getAdminMembers().add(personId);
                } else if ("REJECTED".equals(status)) {
                    replaceMember(teamId, Department.ADMINISTRATION, personId);
                }
            }
            default -> throw new RuntimeException("Invalid department");
        }

        caseDetailsRepository.save(caseDetails);
        teamFormationRepository.save(teamFormation);

        if (isTeamReady(teamFormation) || hasRejections(teamFormation)) {
            handleTeamDecision(teamFormation);
        }
    }

    private void replaceMember(UUID teamId, Department department, UUID rejectedPersonId) {
        TeamFormation teamFormation = teamFormationRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + teamId));
        ChildMarriageCase caseEntity = teamFormation.getCaseId();
        CaseDetails caseDetails = caseEntity.getCaseDetails().get(0);
        List<Person> availableMembers = personRepository.findByDepartmentAndDistrictAndRole(department, caseEntity.getDistrict(), Role.MEMBER)
                .stream().filter(p -> !teamFormation.getMemberIds().contains(p.getId()) && !p.getId().equals(rejectedPersonId))
                .collect(Collectors.toList());

        if (!availableMembers.isEmpty()) {
            Person replacement = availableMembers.get(0);
            teamFormation.getMemberIds().remove(rejectedPersonId);
            teamFormation.getMemberIds().add(replacement.getId());
            teamFormationRepository.save(teamFormation);
            // Notify replacement (placeholder)
            System.out.println("Notified replacement: " + replacement.getId());
        } else {
            escalateCase(teamId);
        }
    }

    private void escalateCase(UUID teamId) {
        TeamFormation teamFormation = teamFormationRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + teamId));
        System.out.println("Escalating case " + teamFormation.getCaseId().getId() + " to district/state authorities");
        // Implement escalation logic (e.g., notify admin)
    }

    public TeamFormationDTO getTeamFormationByCaseId(UUID caseId) {
        TeamFormation teamFormation = teamFormationRepository.findByCaseId_Id(caseId)
                .orElseThrow(() -> new RuntimeException("Team formation not found for case ID: " + caseId));
        return mapToDTO(teamFormation);
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
            escalateCase(teamFormation.getId());
        }
    }

    private TeamFormationDTO mapToDTO(TeamFormation teamFormation) {
        TeamFormationDTO dto = new TeamFormationDTO();
        dto.setCaseId(teamFormation.getCaseId().getId());
        dto.setSupervisorId(teamFormation.getSupervisor().getId());
        dto.setMemberIds(teamFormation.getMemberIds());
        dto.setFormedAt(teamFormation.getFormedAt());
        dto.setPoliceStatus(teamFormation.getPoliceStatus());
        dto.setDiceStatus(teamFormation.getDiceStatus());
        dto.setAdminStatus(teamFormation.getAdminStatus());
        return dto;
    }
}