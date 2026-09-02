package com.my.financetracker.models.requests;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String email;
}
