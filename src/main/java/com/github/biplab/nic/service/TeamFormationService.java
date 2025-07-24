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

    private static final int MEMBERS_PER_DEPARTMENT = 2;
    private static final int MAX_ELIGIBLE_PER_DEPT = 8; // Limit to first 8 if >8 eligible (per ❗)
    private static final long ACCEPTANCE_TIMEOUT_MINUTES = 1440; // 24 hours (per ❗)

    public void initiateTeamFormation(UUID caseId, String subdivision) {
        ChildMarriageCase caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // Extract subdivision from caseDetails.girlSubdivision (fallback to provided)
        String targetSubdivision = subdivision;
        if (caseEntity.getCaseDetails() != null && !caseEntity.getCaseDetails().isEmpty()) {
            CaseDetails caseDetails = caseEntity.getCaseDetails().get(0);
            if (caseDetails.getGirlSubdivision() != null) {
                targetSubdivision = caseDetails.getGirlSubdivision();
            }
        }

        // Fetch ALL departments in the system (not fixed/selected; per your query)
        List<String> allDepts = departmentRepository.findAll().stream()
                .map(Departments::getName)
                .collect(Collectors.toList());
        if (allDepts.isEmpty()) {
            throw new RuntimeException("No departments available in the system");
        }

        // Find all supervisors in the subdivision (notify all; first to accept gets assigned)
        List<Person> allSupervisors = personRepository.findByRoleAndSubdivision(Role.SUPERVISOR, targetSubdivision);
        if (allSupervisors.isEmpty()) {
            throw new RuntimeException("No supervisors available in subdivision: " + targetSubdivision);
        }

        // Fetch eligible members (rank >=2) for each department in the subdivision, limit to first MAX_ELIGIBLE_PER_DEPT if >8
        Map<String, List<UUID>> deptMembersMap = new HashMap<>();
        List<UUID> allMemberIds = new ArrayList<>();
        List<UUID> allSupervisorIds = allSupervisors.stream().map(Person::getId).collect(Collectors.toList());

        for (String deptName : allDepts) {
            List<Person> members = personRepository.findByDepartmentAndSubdivisionAndRoleAndRankGreaterThanEqual(
                    deptName, targetSubdivision, Role.MEMBER, 2);
            // Limit to first MAX_ELIGIBLE_PER_DEPT if more than that
            if (members.size() > MAX_ELIGIBLE_PER_DEPT) {
                members = members.subList(0, MAX_ELIGIBLE_PER_DEPT);
            }
            List<UUID> memberIds = members.stream().map(Person::getId).collect(Collectors.toList());
            if (!memberIds.isEmpty()) { // Only include depts with members
                deptMembersMap.put(deptName, memberIds);
                allMemberIds.addAll(memberIds);
            }
        }

        if (deptMembersMap.isEmpty()) {
            throw new RuntimeException("No eligible members in subdivision: " + targetSubdivision);
        }

        // Create TeamFormation
        TeamFormation teamFormation = new TeamFormation();
        teamFormation.setCaseId(caseEntity);
        teamFormation.setSupervisor(null); // Set when first supervisor accepts
        teamFormation.setMemberIds(new ArrayList<>());
        teamFormation.setNotificationSentAt(LocalDateTime.now());

        Map<String, String> statuses = new HashMap<>();
        for (String deptName : deptMembersMap.keySet()) {
            statuses.put(deptName, "PENDING");
        }
        teamFormation.setDepartmentStatuses(statuses);
        teamFormation.setDepartmentMembers(new HashMap<>());

        TeamFormation savedTeam = teamFormationRepository.save(teamFormation);
        caseEntity.setTeamFormation(savedTeam);
        caseRepository.save(caseEntity);

        // Update CaseDetails
        CaseDetails caseDetails = caseEntity.getCaseDetails().isEmpty() ? new CaseDetails() : caseEntity.getCaseDetails().get(0);
        caseDetails.setCaseId(caseEntity);
        caseDetails.setTeamId(savedTeam.getTeamId());
        caseDetails.setDepartmentMembers(new HashMap<>());
        caseDetailsRepository.save(caseDetails);

        if (caseEntity.getCaseDetails().isEmpty()) {
            caseEntity.getCaseDetails().add(caseDetails);
        }
        caseRepository.save(caseEntity);

        // Notify all eligible members and supervisors
        sendNotifications(savedTeam.getTeamId(), allMemberIds, allSupervisorIds, deptMembersMap, allDepts);

        // TEMPORARY: Auto-assign for testing (first available as if they accepted; per your request)
        autoAssignTeamMembers(savedTeam, deptMembersMap, allSupervisors);
    }

    /**
     * TEMPORARY AUTO-ASSIGNMENT FOR TESTING
     * Auto-assigns first supervisor and first MEMBERS_PER_DEPARTMENT members per dept as if they accepted.
     */
    private void autoAssignTeamMembers(TeamFormation teamFormation, Map<String, List<UUID>> deptMembersMap, List<Person> allSupervisors) {
        // Auto-assign first supervisor
        if (!allSupervisors.isEmpty()) {
            Person firstSupervisor = allSupervisors.get(0);
            teamFormation.setSupervisor(firstSupervisor);

            TeamResponse supervisorResponse = new TeamResponse();
            supervisorResponse.setTeamId(teamFormation.getTeamId());
            supervisorResponse.setPersonId(firstSupervisor.getId());
            supervisorResponse.setResponse("ACCEPTED");
            supervisorResponse.setRespondedAt(LocalDateTime.now());
            teamResponseRepository.save(supervisorResponse);
        }

        // Auto-assign members per department (up to MEMBERS_PER_DEPARTMENT)
        Map<String, List<UUID>> finalDepartmentMembers = new HashMap<>();
        List<UUID> allAssignedMembers = new ArrayList<>();

        for (Map.Entry<String, List<UUID>> entry : deptMembersMap.entrySet()) {
            String department = entry.getKey();
            List<UUID> availableMembers = entry.getValue();
            List<UUID> assignedToDept = new ArrayList<>();
            int assignedCount = 0;

            for (UUID memberId : availableMembers) {
                if (assignedCount < MEMBERS_PER_DEPARTMENT) {
                    assignedToDept.add(memberId);
                    allAssignedMembers.add(memberId);
                    assignedCount++;

                    TeamResponse memberResponse = new TeamResponse();
                    memberResponse.setTeamId(teamFormation.getTeamId());
                    memberResponse.setPersonId(memberId);
                    memberResponse.setResponse("ACCEPTED");
                    memberResponse.setRespondedAt(LocalDateTime.now());
                    teamResponseRepository.save(memberResponse);
                } else {
                    // Reject excess for testing
                    TeamResponse memberResponse = new TeamResponse();
                    memberResponse.setTeamId(teamFormation.getTeamId());
                    memberResponse.setPersonId(memberId);
                    memberResponse.setResponse("REJECTED");
                    memberResponse.setRespondedAt(LocalDateTime.now());
                    teamResponseRepository.save(memberResponse);
                }
            }

            if (!assignedToDept.isEmpty()) {
                finalDepartmentMembers.put(department, assignedToDept);
                teamFormation.getDepartmentStatuses().put(department, "ACCEPTED");
            }
        }

        teamFormation.setMemberIds(allAssignedMembers);
        teamFormation.setDepartmentMembers(finalDepartmentMembers);
        teamFormation.setFormedAt(LocalDateTime.now());
        teamFormationRepository.save(teamFormation);

        // Update case details and status
        ChildMarriageCase caseEntity = teamFormation.getCaseId();
        CaseDetails caseDetails = caseEntity.getCaseDetails().get(0);
        caseDetails.setDepartmentMembers(finalDepartmentMembers);
        if (teamFormation.getSupervisor() != null) {
            caseDetails.setSupervisorId(teamFormation.getSupervisor().getId());
        }
        caseDetailsRepository.save(caseDetails);

        caseEntity.setStatus("IN_PROGRESS");
        caseRepository.save(caseEntity);

        System.out.println("Team auto-formed for testing: " + finalDepartmentMembers);
    }

    public void handleResponse(UUID teamId, UUID personId, String department, String status) {
        TeamFormation teamFormation = teamFormationRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        TeamResponse response = teamResponseRepository.findByTeamIdAndPersonId(teamId, personId)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        // Check if team is already formed (no total size limit, but respect per-dept caps)
        if (teamFormation.getFormedAt() != null) {
            throw new RuntimeException("Team is already formed. Cannot add more members.");
        }

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        if ("ACCEPTED".equals(status)) {
            if (person.getRole() == Role.SUPERVISOR) {
                // First supervisor to accept
                if (teamFormation.getSupervisor() == null) {
                    teamFormation.setSupervisor(person);
                } else {
                    response.setResponse("REJECTED");
                    response.setRespondedAt(LocalDateTime.now());
                    teamResponseRepository.save(response);
                    throw new RuntimeException("Supervisor already assigned.");
                }
            } else {
                // Check per-department limit (no total team limit)
                Map<String, List<UUID>> deptMembers = teamFormation.getDepartmentMembers();
                List<UUID> currentDeptMembers = deptMembers.getOrDefault(department, new ArrayList<>());
                if (currentDeptMembers.size() >= MEMBERS_PER_DEPARTMENT) {
                    response.setResponse("REJECTED");
                    response.setRespondedAt(LocalDateTime.now());
                    teamResponseRepository.save(response);
                    throw new RuntimeException("Department limit reached. Cannot add more.");
                }

                // Add member
                currentDeptMembers.add(personId);
                deptMembers.put(department, currentDeptMembers);
                teamFormation.setDepartmentMembers(deptMembers);

                List<UUID> allMembers = teamFormation.getMemberIds();
                allMembers.add(personId);
                teamFormation.setMemberIds(allMembers);
            }
        }

        response.setResponse(status.toUpperCase());
        response.setRespondedAt(LocalDateTime.now());
        teamResponseRepository.save(response);

        updateDepartmentStatus(teamFormation, department);
        teamFormationRepository.save(teamFormation);

        if (isTeamComplete(teamFormation)) {
            confirmTeamFormation(teamFormation);
        }
    }

    private boolean isTeamComplete(TeamFormation team) {
        if (team.getSupervisor() == null) return false;

        // Complete if all depts have MEMBERS_PER_DEPARTMENT (or less if no more available)
        for (String dept : team.getDepartmentStatuses().keySet()) {
            List<UUID> deptMembers = team.getDepartmentMembers().getOrDefault(dept, new ArrayList<>());
            if (deptMembers.size() < MEMBERS_PER_DEPARTMENT && !"NO_MEMBERS".equals(team.getDepartmentStatuses().get(dept))) {
                return false;
            }
        }
        return true;
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

    @Scheduled(fixedRate = 60000) // Check every minute
    public void checkAcceptanceStatus() {
        List<TeamFormation> pendingTeams = teamFormationRepository.findByFormedAtIsNull();
        for (TeamFormation team : pendingTeams) {
            LocalDateTime timeout = team.getNotificationSentAt().plusMinutes(ACCEPTANCE_TIMEOUT_MINUTES);
            if (LocalDateTime.now().isAfter(timeout)) {
                for (String dept : team.getDepartmentStatuses().keySet()) {
                    if ("PENDING".equals(team.getDepartmentStatuses().get(dept))) {
                        handleDepartmentalEscalation(team, dept);
                    }
                }
            }
            if (isTeamComplete(team)) {
                confirmTeamFormation(team);
            }
        }
    }

    private void handleDepartmentalEscalation(TeamFormation team, String department) {
        String subdivision = team.getCaseId().getCaseDetails().get(0).getGirlSubdivision();
        // Escalate to rank 1 supervisor from SAME department only
        List<Person> rank1Supervisors = personRepository.findByDepartmentAndSubdivisionAndRoleAndRank(department, subdivision, Role.SUPERVISOR, 1);
        if (!rank1Supervisors.isEmpty()) {
            Person escalator = rank1Supervisors.get(0);
            // Assign as a member to the team
            List<UUID> currentDeptMembers = team.getDepartmentMembers().getOrDefault(department, new ArrayList<>());
            if (currentDeptMembers.size() < MEMBERS_PER_DEPARTMENT) {
                currentDeptMembers.add(escalator.getId());
                team.getDepartmentMembers().put(department, currentDeptMembers);
                team.getMemberIds().add(escalator.getId());
                team.getDepartmentStatuses().put(department, "ESCALATED");

                TeamResponse response = new TeamResponse();
                response.setTeamId(team.getTeamId());
                response.setPersonId(escalator.getId());
                response.setResponse("ACCEPTED");
                response.setRespondedAt(LocalDateTime.now());
                teamResponseRepository.save(response);

                teamFormationRepository.save(team);
                System.out.println("Escalated " + department + " to Rank 1 supervisor: " + escalator.getId());
            }
        }
    }

    private void sendNotifications(UUID teamId, List<UUID> memberIds, List<UUID> supervisorIds,
                                   Map<String, List<UUID>> deptMembersMap, List<String> selectedDepts) {
        // Notify supervisors
        for (UUID supervisorId : supervisorIds) {
            TeamResponse response = new TeamResponse();
            response.setTeamId(teamId);
            response.setPersonId(supervisorId);
            response.setResponse("PENDING");
            response.setRespondedAt(null);
            teamResponseRepository.save(response);
        }

        // Notify members
        for (UUID personId : memberIds) {
            TeamResponse response = new TeamResponse();
            response.setTeamId(teamId);
            response.setPersonId(personId);
            response.setResponse("PENDING");
            response.setRespondedAt(null);
            teamResponseRepository.save(response);
        }

        System.out.println("Notifications sent for team " + teamId + " to supervisors: " + supervisorIds + " and members: " + memberIds);
    }

    private void updateDepartmentStatus(TeamFormation team, String department) {
        List<TeamResponse> deptResponses = teamResponseRepository.findByTeamId(team.getTeamId()).stream()
                .filter(r -> getDepartmentForPerson(r.getPersonId(), team.getDepartmentMembers()).equals(department))
                .collect(Collectors.toList());

        if (deptResponses.stream().allMatch(r -> "ACCEPTED".equals(r.getResponse()))) {
            team.getDepartmentStatuses().put(department, "ACCEPTED");
        } else if (deptResponses.stream().allMatch(r -> "REJECTED".equals(r.getResponse()))) {
            team.getDepartmentStatuses().put(department, "REJECTED");
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
        if (team.getSupervisor() != null) {
            dto.setSupervisorId(team.getSupervisor().getId());
        }
        dto.setDepartmentMembers(team.getDepartmentMembers());
        dto.setFormedAt(team.getFormedAt());
        dto.setDepartmentStatuses(team.getDepartmentStatuses());
        return dto;
    }
}
