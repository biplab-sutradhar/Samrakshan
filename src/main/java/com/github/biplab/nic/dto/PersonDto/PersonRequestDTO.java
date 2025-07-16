package com.github.biplab.nic.dto.PersonDto;

import com.github.biplab.nic.enums.Department;
import com.github.biplab.nic.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String gender;

    private String address;

    @NotBlank(message = "Role is required")
    private Role role;

    private Department department;

    @NotBlank(message = "Password is required")
    private String password;

    private String district;
    private String designation;
    private String officeName;
    private String status;
    private String subdivision;
    private Integer rank;

}