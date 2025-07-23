package com.github.biplab.nic.service;

import com.github.biplab.nic.dto.TeamFormationDTO;
import com.github.biplab.nic.entity.ChildMarriageCase;
import com.github.biplab.nic.entity.CaseDetails;
import com.github.biplab.nic.entity.Departments;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.entity.TeamResponse;
import com.github.biplab.nic.enums.Role;
import com.github.biplab.nic.repository.CaseDetailsRepository;
import com.github.biplab.nic.repository.CaseRepository;
import com.github.biplab.nic.repository.DepartmentRepository;
import com.github.biplab.nic.repository.PersonRepository;
import com.github.biplab.nic.repository.TeamFormationRepository;
import com.github.biplab.nic.repository.TeamResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private DepartmentRepository departmentRepository;

    private static final int MAX_TEAM_SIZE = 8;
    private static final long ACCEPTANCE_TIMEOUT_MINUTES = 60;  // 1 hour

    public void initiateTeamFormation(UUID caseId, String district, String subdivision) {
        ChildMarriageCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // Select 1 supervisor (rank=1) from subdivision (any department)
        List<Person> supervisors = personRepository.findByRoleAndSubdivision(Role.SUPERVISOR, subdivision);
        if (supervisors.isEmpty()) {
            throw new RuntimeException("No supervisor available in subdivision: " + subdivision);
        }
        Person supervisor = supervisors.get(0);  // Select first or customize

        // Fetch all relevant departments in the subdivision
        List<String> selectedDepts = getDepartmentsBySubdivision(subdivision);

        // Fetch eligible members (rank >=2) for each department in subdivision
        Map<String, List<UUID>> deptMembersMap = new HashMap<>();
        List<UUID> allMemberIds = new ArrayList<>();

        for (String deptName : selectedDepts) {
            List<Person> members = personRepository.findByDepartmentAndSubdivisionAndRoleAndRankGreaterThanEqual(deptName, subdivision, Role.MEMBER, 2);
            List<UUID> memberIds = members.stream().map(Person::getId).collect(Collectors.toList());
            deptMembersMap.put(deptName, memberIds);
            allMemberIds.addAll(memberIds);
        }

        // Create TeamFormation
        TeamFormation teamFormation = new TeamFormation();
        teamFormation.setCaseId(caseEntity);
        teamFormation.setSupervisor(supervisor);
        teamFormation.setMemberIds(new ArrayList<>());  // Empty; fill on accept
        teamFormation.setNotificationSentAt(LocalDateTime.now());

        Map<String, String> statuses = new HashMap<>();
        for (String deptName : selectedDepts) {
            statuses.put(deptName, deptMembersMap.get(deptName).isEmpty() ? "NO_MEMBERS" : "PENDING");
        }
        teamFormation.setDepartmentStatuses(statuses);
        teamFormation.setDepartmentMembers(new HashMap<>());  // Empty; update on accept

        TeamFormation savedTeam = teamFormationRepository.save(teamFormation);
        caseEntity.setTeamFormation(savedTeam);
        caseRepository.save(caseEntity);

        // Update CaseDetails
        CaseDetails caseDetails = caseEntity.getCaseDetails().isEmpty() ? new CaseDetails() : caseEntity.getCaseDetails().get(0);
        caseDetails.setCaseId(caseEntity);
        caseDetails.setSupervisorId(supervisor.getId());
        caseDetails.setTeamId(savedTeam.getTeamId());
        caseDetails.setDepartmentMembers(new HashMap<>());  // Empty initially
        caseDetailsRepository.save(caseDetails);
        if (caseEntity.getCaseDetails().isEmpty()) {
            caseEntity.getCaseDetails().add(caseDetails);
        }
        caseRepository.save(caseEntity);

        // Notify all eligible members (not supervisor yet)
        sendNotifications(savedTeam.getTeamId(), allMemberIds, deptMembersMap, selectedDepts);
    }

    private List<String> getDepartmentsBySubdivision(String subdivision) {
        // Fetch all departments (assume all are available; customize if departments are linked to subdivisions)
        List<Departments> allDepts = departmentRepository.findAll();
        return allDepts.stream().map(Departments::getName).collect(Collectors.toList());
    }

    private void sendNotifications(UUID teamId, List<UUID> memberIds, Map<String, List<UUID>> deptMembersMap, List<String> selectedDepts) {
        for (UUID personId : memberIds) {
            TeamResponse response = new TeamResponse();
            response.setTeamId(teamId);
            response.setPersonId(personId);
            response.setResponse("PENDING");
            response.setRespondedAt(null);
            teamResponseRepository.save(response);
        }
        System.out.println("Notifications sent for team " + teamId + " to members: " + memberIds);

        // Temporary simulation for testing
        simulateResponses(teamId, memberIds, deptMembersMap, selectedDepts);
    }

    private void simulateResponses(UUID teamId, List<UUID> memberIds, Map<String, List<UUID>> deptMembersMap, List<String> selectedDepts) {
        Random random = new Random();
        int delay = 1000;  // 1 sec
        for (UUID personId : memberIds) {
            int finalDelay = delay;
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    String dept = getDepartmentForPerson(personId, deptMembersMap);
                    String simulatedResponse = random.nextBoolean() ? "ACCEPTED" : "REJECTED";
                    handleResponse(teamId, personId, dept, simulatedResponse);
                    System.out.println("Simulated response for " + personId + " in " + dept + ": " + simulatedResponse);
                }
            }, finalDelay);

        }
    }

    private String getDepartmentForPerson(UUID personId, Map<String, List<UUID>> deptMembersMap) {
        for (Map.Entry<String, List<UUID>> entry : deptMembersMap.entrySet()) {
            if (entry.getValue().contains(personId)) {
                return entry.getKey();
            }
        }
        return "UNKNOWN";
    }

    public void handleResponse(UUID teamId, UUID personId, String department, String status) {
        TeamFormation teamFormation = teamFormationRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        TeamResponse response = teamResponseRepository.findByTeamIdAndPersonId(teamId, personId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        if (teamFormation.getMemberIds().size() >= MAX_TEAM_SIZE - 1 && "ACCEPTED".equals(status)) {
            response.setResponse("REJECTED");
            response.setRespondedAt(LocalDateTime.now());
            teamResponseRepository.save(response);
            throw new RuntimeException("Team already formed. Cannot accept more members.");
        }

        response.setResponse(status.toUpperCase());
        response.setRespondedAt(LocalDateTime.now());
        teamResponseRepository.save(response);

        if ("ACCEPTED".equals(status)) {
            List<UUID> currentMembers = teamFormation.getMemberIds();
            currentMembers.add(personId);
            teamFormation.setMemberIds(currentMembers);

            Map<String, List<UUID>> deptMembers = teamFormation.getDepartmentMembers();
            List<UUID> deptList = deptMembers.getOrDefault(department, new ArrayList<>());
            deptList.add(personId);
            deptMembers.put(department, deptList);
            teamFormation.setDepartmentMembers(deptMembers);
        }

        updateDepartmentStatus(teamFormation, department);
        teamFormationRepository.save(teamFormation);

        if (isTeamFullyAccepted(teamFormation)) {
            confirmTeamFormation(teamFormation);
        } else if (hasRejections(teamFormation)) {
            handleDepartmentalEscalation(teamFormation);
        }
    }

    private void updateDepartmentStatus(TeamFormation team, String department) {
        List<TeamResponse> deptResponses = teamResponseRepository.findByTeamId(team.getTeamId()).stream()
                .filter(r -> getDepartmentForPerson(r.getPersonId(), team.getDepartmentMembers()).equals(department))
                .collect(Collectors.toList());

        if (deptResponses.stream().allMatch(r -> "ACCEPTED".equals(r.getResponse()))) {
            team.getDepartmentStatuses().put(department, "ACCEPTED");
        } else if (deptResponses.stream().anyMatch(r -> "REJECTED".equals(r.getResponse()))) {
            team.getDepartmentStatuses().put(department, "REJECTED");
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkAcceptanceStatus() {
        List<TeamFormation> pendingTeams = teamFormationRepository.findByFormedAtIsNull();
        for (TeamFormation team : pendingTeams) {
            LocalDateTime timeout = team.getNotificationSentAt().plusMinutes(ACCEPTANCE_TIMEOUT_MINUTES);
            if (LocalDateTime.now().isAfter(timeout)) {
                handleDepartmentalEscalation(team);
            } else if (isTeamFullyAccepted(team)) {
                confirmTeamFormation(team);
            }
        }
    }

    private boolean isTeamFullyAccepted(TeamFormation team) {
        return team.getMemberIds().size() >= MAX_TEAM_SIZE - 1;  // Lock when 7 members + supervisor
    }

    private void confirmTeamFormation(TeamFormation team) {
        team.setFormedAt(LocalDateTime.now());
        teamFormationRepository.save(team);

        ChildMarriageCase caseEntity = team.getCaseId();
        CaseDetails caseDetails = caseEntity.getCaseDetails().get(0);
        caseDetails.setDepartmentMembers(team.getDepartmentMembers());
        caseDetails.setSupervisorId(team.getSupervisor().getId());
        caseDetailsRepository.save(caseDetails);

        caseEntity.setStatus("IN_PROGRESS");
        caseRepository.save(caseEntity);

        System.out.println("Team confirmed for case " + caseEntity.getId());
    }

    private void handleDepartmentalEscalation(TeamFormation team) {
        String district = team.getCaseId().getDistrict();
        String subdivision = team.getCaseId().getCaseDetails().get(0).getGirlSubdivision();

        for (Map.Entry<String, String> entry : team.getDepartmentStatuses().entrySet()) {
            if ("PENDING".equals(entry.getValue()) || "NO_MEMBERS".equals(entry.getValue()) || hasFullRejection(team, entry.getKey())) {
                String deptName = entry.getKey();
                List<Person> rank1Supervisors = personRepository.findByDepartmentAndSubdivisionAndRoleAndRank(deptName, subdivision, Role.SUPERVISOR, 1);
                if (!rank1Supervisors.isEmpty()) {
                    Person escalator = rank1Supervisors.get(0);
                    // For auto-assign (or call manualAssign for manual)
                    handleResponse(team.getTeamId(), escalator.getId(), deptName, "ACCEPTED");
                    System.out.println("Escalated " + deptName + " to Rank 1: " + escalator.getId());
                }
            }
        }
    }

    private boolean hasFullRejection(TeamFormation team, String department) {
        List<TeamResponse> deptResponses = teamResponseRepository.findByTeamId(team.getTeamId()).stream()
                .filter(r -> getDepartmentForPerson(r.getPersonId(), team.getDepartmentMembers()).equals(department))
                .collect(Collectors.toList());
        return deptResponses.stream().allMatch(r -> "REJECTED".equals(r.getResponse()));
    }

    private boolean hasRejections(TeamFormation team) {
        return team.getDepartmentStatuses().values().stream().anyMatch("REJECTED"::equals);
    }

    // Manual assignment by Rank 1
    public void manualAssign(UUID teamId, UUID rank1PersonId, UUID assignedPersonId, String department) {
        Person rank1 = personRepository.findById(rank1PersonId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        if (rank1.getRank() != 1 || !rank1.getRole().equals(Role.SUPERVISOR)) {
            throw new RuntimeException("Only Rank 1 supervisors can assign");
        }

        handleResponse(teamId, assignedPersonId, department, "ACCEPTED");
        System.out.println("Assigned " + assignedPersonId + " to team " + teamId + " by " + rank1PersonId);
    }

    @Deprecated
    public TeamFormationDTO createTeamFormation(TeamFormationDTO teamFormationDTO) {
        throw new UnsupportedOperationException("Manual creation deprecated");
    }

    public TeamFormationDTO getTeamFormationById(UUID id) {
        TeamFormation team = teamFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return mapToDTO(team);
    }

    public TeamFormationDTO getTeamFormationByCaseId(UUID caseId) {
        TeamFormation team = teamFormationRepository.findByCaseId_Id(caseId)
                .orElseThrow(() -> new RuntimeException("Team not found for case"));
        return mapToDTO(team);
    }

    private TeamFormationDTO mapToDTO(TeamFormation team) {
        TeamFormationDTO dto = new TeamFormationDTO();
        dto.setCaseId(team.getCaseId().getId());
        dto.setSupervisorId(team.getSupervisor().getId());
        dto.setDepartmentMembers(team.getDepartmentMembers());
        dto.setFormedAt(team.getFormedAt());
        dto.setDepartmentStatuses(team.getDepartmentStatuses());
        return dto;
    }
}
