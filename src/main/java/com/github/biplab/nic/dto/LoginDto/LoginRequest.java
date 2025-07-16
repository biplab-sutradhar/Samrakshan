package com.github.biplab.nic.dto.LoginDto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}