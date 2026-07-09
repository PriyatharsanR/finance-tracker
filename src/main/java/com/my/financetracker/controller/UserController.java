package com.my.financetracker.controller;

import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.models.responses.UserResponse;
import com.my.financetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public DefaultResponse<UserResponse> getCurrentUserProfile() {
        return userService.getCurrentUserProfile();
    }
}