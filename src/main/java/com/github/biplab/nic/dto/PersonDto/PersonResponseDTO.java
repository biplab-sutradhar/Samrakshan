package com.github.biplab.nic.dto.PersonDto;

import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String address;
    private String role;
    private String department;
    private Integer rank;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String district;
    private String designation;
    private String officeName;
    private String status;
    private String subdivision;
    private String postName;
}
