package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.TeamFormation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamFormationRepository extends JpaRepository<TeamFormation, UUID> {
    Optional<TeamFormation> findByCaseId_Id(UUID caseId);
}