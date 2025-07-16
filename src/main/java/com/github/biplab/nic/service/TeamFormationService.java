package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.CaseDetails;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.entity.TeamResponse;
import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role;
import com.github.biplab.nic.repository.CaseDetailsRepository;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.TeamFormationRepository;
import com.github.biplab.nic.repository.TeamResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    @Autowired
    private TeamResponseRepository teamResponseRepository;

    private static final int MAX_TEAM_SIZE = 8; // Maximum team size including supervisor
    private static final long ACCEPTANCE_TIMEOUT_HOURS = 24;

    public void initiateTeamFormation(UUID caseId, String district) {
        ChildMarriageCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // Select any available supervisor
        List<Person> supervisors = personRepository.findByRoleAndDistrict(Role.SUPERVISOR, district);
        if (supervisors.isEmpty()) {
            throw new RuntimeException("No supervisor available in district: " + district);
        }
        Person supervisor = supervisors.get(0);

        // Select up to 2 members per department, limiting total to 8 including supervisor
        List<Person> policeMembers = personRepository.findByDepartmentAndDistrictAndRoleAndRank(Department.POLICE, district, Role.MEMBER, 2)
                .stream().limit(2).collect(Collectors.toList());
        List<Person> diceMembers = personRepository.findByDepartmentAndDistrictAndRoleAndRank(Department.DICE, district, Role.MEMBER, 2)
                .stream().limit(2).collect(Collectors.toList());
        List<Person> adminMembers = personRepository.findByDepartmentAndDistrictAndRoleAndRank(Department.ADMINISTRATION, district, Role.MEMBER, 2)
                .stream().limit(2).collect(Collectors.toList());

        // Total members (6) + supervisor (1) = 7, within MAX_TEAM_SIZE (8)
        List<UUID> allMemberIds = new ArrayList<>();
        allMemberIds.addAll(policeMembers.stream().map(Person::getId).collect(Collectors.toList()));
        allMemberIds.addAll(diceMembers.stream().map(Person::getId).collect(Collectors.toList()));
        allMemberIds.addAll(adminMembers.stream().map(Person::getId).collect(Collectors.toList()));

        // Create pending TeamFormation
        TeamFormation teamFormation = new TeamFormation();
        teamFormation.setCaseId(caseEntity);
        teamFormation.setSupervisor(supervisor);
        teamFormation.setMemberIds(allMemberIds);
        teamFormation.setNotificationSentAt(LocalDateTime.now());
        teamFormation.setPoliceStatus("PENDING");
        teamFormation.setDiceStatus("PENDING");
        teamFormation.setAdminStatus("PENDING");
        TeamFormation savedTeam = teamFormationRepository.save(teamFormation);
        caseEntity.setTeamFormation(savedTeam);
        caseRepository.save(caseEntity);

        System.out.println("TeamFormation initiated for case " + caseId + " with supervisor " + supervisor.getId() + " and members " + allMemberIds);

        // Update CaseDetails with pending state
        CaseDetails caseDetails = caseEntity.getCaseDetails().isEmpty() ? new CaseDetails() : caseEntity.getCaseDetails().get(0);
        caseDetails.setCaseId(caseEntity);
        caseDetails.setSupervisorId(supervisor.getId());
        caseDetails.setTeamId(savedTeam.getTeamId());
        caseDetailsRepository.save(caseDetails);
        if (caseEntity.getCaseDetails().isEmpty()) {
            caseEntity.getCaseDetails().add(caseDetails);
        }
        caseRepository.save(caseEntity);

        // Send notifications
        sendNotifications(savedTeam.getTeamId(), supervisor.getId(), allMemberIds);
    }

    private void sendNotifications(UUID teamId, UUID supervisorId, List<UUID> memberIds) {
        List<UUID> allIds = new ArrayList<>();
        allIds.add(supervisorId);
        allIds.addAll(memberIds);
        if (allIds.size() >= MAX_TEAM_SIZE) {
            System.out.println("Team for " + teamId + " is already formed with " + allIds.size() + " members. No more members can be added.");
            return; // Prevent further additions
        }

        for (UUID personId : allIds) {
            TeamResponse response = new TeamResponse();
            response.setTeamId(teamId);
            response.setPersonId(personId);
            response.setResponse("PENDING");
            teamResponseRepository.save(response);
        }
        System.out.println("Notifications sent for team " + teamId + " to: " + allIds);

        // Simulate member rejection or acceptance
        Random random = new Random();
        for (UUID personId : allIds) {
            String initialResponse = random.nextBoolean() ? "REJECTED" : "PENDING";
            if ("REJECTED".equals(initialResponse)) {
                handleResponse(teamId, personId, getDepartmentForPerson(personId, memberIds), "REJECTED");
                System.out.println("Member " + personId + " rejected case for team " + teamId);
            }
        }

        // Sequential acceptance after 1-second initial delay
        int delay = 1000;
        for (UUID personId : allIds) {
            TeamResponse response = teamResponseRepository.findByTeamIdAndPersonId(teamId, personId)
                    .orElseThrow(() -> new RuntimeException("Response not found for person ID: " + personId));
            if ("PENDING".equals(response.getResponse())) {
                int finalDelay = delay;
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        handleResponse(teamId, personId, getDepartmentForPerson(personId, memberIds), "ACCEPTED");
                        System.out.println("Member " + personId + " accepted case for team " + teamId + " after " + (finalDelay / 1000) + " seconds");
                    }
                }, delay);
                delay += 1000;
            }
        }
    }

    private String getDepartmentForPerson(UUID personId, List<UUID> memberIds) {
        int index = memberIds.indexOf(personId);
        if (index < 2) return "POLICE";
        else if (index < 4) return "DICE";
        else return "ADMINISTRATION";
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    public void checkAcceptanceStatus() {
        List<TeamFormation> pendingTeams = teamFormationRepository.findByFormedAtIsNull();
        for (TeamFormation team : pendingTeams) {
            LocalDateTime timeout = team.getNotificationSentAt().plusHours(ACCEPTANCE_TIMEOUT_HOURS);
            if (LocalDateTime.now().isAfter(timeout)) {
                handleDepartmentalEscalation(team); // Use departmental escalation
            } else if (isTeamFullyAccepted(team)) {
                confirmTeamFormation(team);
            }
        }
    }

    private boolean isTeamFullyAccepted(TeamFormation team) {
        List<TeamResponse> responses = teamResponseRepository.findByTeamId(team.getTeamId());
        return responses.stream().allMatch(r -> "ACCEPTED".equals(r.getResponse()));
    }

    private void confirmTeamFormation(TeamFormation team) {
        team.setFormedAt(LocalDateTime.now());
        TeamFormation savedTeam = teamFormationRepository.save(team);

        ChildMarriageCase caseEntity = team.getCaseId();
        CaseDetails caseDetails = caseEntity.getCaseDetails().get(0);
        List<UUID> memberIds = team.getMemberIds();
        caseDetails.setPoliceMembers(memberIds.subList(0, 2));
        caseDetails.setDiceMembers(memberIds.subList(2, 4));
        caseDetails.setAdminMembers(memberIds.subList(4, 6));
        caseDetailsRepository.save(caseDetails);
        caseEntity.setStatus("IN_PROGRESS");
        caseRepository.save(caseEntity);

        System.out.println("TeamFormation confirmed for case " + team.getCaseId().getId() + " with members assigned: " +
                caseDetails.getPoliceMembers() + ", " + caseDetails.getDiceMembers() + ", " + caseDetails.getAdminMembers());
    }

    private void handleDepartmentalEscalation(TeamFormation team) {
        ChildMarriageCase caseEntity = team.getCaseId();
        CaseDetails caseDetails = caseEntity.getCaseDetails().get(0);
        String district = caseEntity.getDistrict();

        // Check each department for unresponsiveness
        if ("PENDING".equals(team.getPoliceStatus())) {
            escalateDepartment(team, Department.POLICE, district);
        }
        if ("PENDING".equals(team.getDiceStatus())) {
            escalateDepartment(team, Department.DICE, district);
        }
        if ("PENDING".equals(team.getAdminStatus())) {
            escalateDepartment(team, Department.ADMINISTRATION, district);
        }
    }

    private void escalateDepartment(TeamFormation team, Department department, String district) {
        List<Person> higherRankMembers = personRepository.findByDepartmentAndDistrictAndRoleAndRank(department, district, Role.SUPERVISOR, 1);
        Person assignedPerson = higherRankMembers.isEmpty() ? null : higherRankMembers.get(0);

        if (assignedPerson == null) {
            System.out.println("No rank 1 supervisor found in " + department + " department for district " + district);
            return;
        }

        // Update case details with escalation note
        CaseDetails caseDetails = team.getCaseId().getCaseDetails().get(0);
        caseDetails.setNotes(caseDetails.getNotes() + "\nEscalated to " + assignedPerson.getId() + " in " + department + " department");
        caseDetailsRepository.save(caseDetails);
        System.out.println("Case " + team.getCaseId().getId() + " escalated to " + assignedPerson.getId() + " in " + department + " department");
    }

    public void handleResponse(UUID teamId, UUID personId, String department, String status) {
        TeamFormation teamFormation = teamFormationRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + teamId));
        TeamResponse response = teamResponseRepository.findByTeamIdAndPersonId(teamId, personId)
                .orElseThrow(() -> new RuntimeException("Response not found for person ID: " + personId));
        response.setResponse(status.toUpperCase());
        response.setRespondedAt(LocalDateTime.now());
        teamResponseRepository.save(response);

        switch (department.toUpperCase()) {
            case "POLICE" -> teamFormation.setPoliceStatus(status.toUpperCase());
            case "DICE" -> teamFormation.setDiceStatus(status.toUpperCase());
            case "ADMINISTRATION" -> teamFormation.setAdminStatus(status.toUpperCase());
            default -> throw new RuntimeException("Invalid department");
        }
        teamFormationRepository.save(teamFormation);

        System.out.println("Response received for team " + teamId + ": " + personId + " in " + department + " - " + status);
        if (isTeamFullyAccepted(teamFormation)) {
            confirmTeamFormation(teamFormation);
        } else if (hasRejections(teamFormation)) {
            handleDepartmentalEscalation(teamFormation); // Immediate escalation on rejection
        }
    }

    private boolean hasRejections(TeamFormation team) {
        return "REJECTED".equals(team.getPoliceStatus()) || "REJECTED".equals(team.getDiceStatus()) || "REJECTED".equals(team.getAdminStatus());
    }

    @Deprecated
    public TeamFormationDTO createTeamFormation(TeamFormationDTO teamFormationDTO) {
        throw new UnsupportedOperationException("Manual team creation is deprecated. Use initiateTeamFormation instead.");
    }

    public TeamFormationDTO getTeamFormationById(UUID id) {
        TeamFormation teamFormation = teamFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team formation not found with ID: " + id));
        return mapToDTO(teamFormation);
    }

    public TeamFormationDTO getTeamFormationByCaseId(UUID caseId) {
        TeamFormation teamFormation = teamFormationRepository.findByCaseId_Id(caseId)
                .orElseThrow(() -> new RuntimeException("Team formation not found for case ID: " + caseId));
        return mapToDTO(teamFormation);
    }

    private TeamFormationDTO mapToDTO(TeamFormation teamFormation) {
        TeamFormationDTO dto = new TeamFormationDTO();
        dto.setCaseId(teamFormation.getCaseId().getId());
        dto.setSupervisorId(teamFormation.getSupervisor().getId());
        List<UUID> memberIds = teamFormation.getMemberIds();
        dto.setPoliceMembers(memberIds.subList(0, 2));
        dto.setDiceMembers(memberIds.subList(2, 4));
        dto.setAdminMembers(memberIds.subList(4, 6));
        dto.setFormedAt(teamFormation.getFormedAt());
        dto.setPoliceStatus(teamFormation.getPoliceStatus());
        dto.setDiceStatus(teamFormation.getDiceStatus());
        dto.setAdminStatus(teamFormation.getAdminStatus());
        return dto;
    }
}