package com.my.financetracker.controller;

import com.my.financetracker.models.requests.LoginRequest;
import com.my.financetracker.models.requests.RegisterRequest;
import com.my.financetracker.models.responses.AuthResponse;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public DefaultResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}
