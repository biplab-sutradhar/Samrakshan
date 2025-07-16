package com.github.biplab.nic.dto.LoginDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private UUID userId;
}
