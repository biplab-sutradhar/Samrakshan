package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.ChildMarriageCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaseRepository extends JpaRepository<ChildMarriageCase, UUID> {
}