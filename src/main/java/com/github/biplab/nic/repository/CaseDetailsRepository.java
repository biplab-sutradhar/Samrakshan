package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.CaseDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaseDetailsRepository extends JpaRepository<CaseDetails, UUID> {
}