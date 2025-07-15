package com.github.biplab.nic.controller;

import com.github.biplab.nic.dto.CaseDto.CaseRequestDTO;
import com.github.biplab.nic.dto.CaseDto.CaseResponseDTO;
import com.github.biplab.nic.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @PostMapping
    public ResponseEntity<CaseResponseDTO> submitCase(@RequestBody CaseRequestDTO caseRequestDTO) {
        CaseResponseDTO response = caseService.submitCase(caseRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponseDTO> getCaseById(@PathVariable UUID id) {
        CaseResponseDTO response = caseService.getCaseById(id);
        return ResponseEntity.ok(response);
    }


}