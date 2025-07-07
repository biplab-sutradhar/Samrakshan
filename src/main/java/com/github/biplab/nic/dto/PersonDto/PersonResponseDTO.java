package com.github.biplab.nic.dto.PersonDto;

import com.github.biplab.nic.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponseDTO {
    private UUID id;
    private String name;
    private String contactNo;
    private Role role;
}