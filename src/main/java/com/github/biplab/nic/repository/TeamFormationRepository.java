package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.TeamFormation;
import com.github.biplab.nic.entity.TeamResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamFormationRepository extends JpaRepository<TeamFormation, UUID> {
    List<TeamFormation> findByFormedAtIsNull();
    Optional<TeamFormation> findByCaseId_Id(UUID caseId);

    @Query("SELECT t FROM TeamFormation t WHERE t.departmentStatuses[:deptName] = :status")
    List<TeamFormation> findByDepartmentStatus(String deptName, String status);


}