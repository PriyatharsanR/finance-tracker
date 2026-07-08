package com.my.financetracker.models.responses;

import com.my.financetracker.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private Long userId;
    private String name;
    private String email;
    private Role role;
    private String token;
}
