package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.TeamResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamResponseRepository extends JpaRepository<TeamResponse, UUID> {
    // Existing methods
    Optional<TeamResponse> findByTeamIdAndPersonId(UUID teamId, UUID personId);

    List<TeamResponse> findByTeamId(UUID teamId);

    // New method to find responses by teamId and response
    @Query("SELECT tr FROM TeamResponse tr WHERE tr.teamId = ?1 AND tr.response = ?2")
    List<TeamResponse> findByTeamIdAndResponse(UUID teamId, String response);
    List<TeamResponse> findByResponse(String response);
}