package com.my.financetracker.service;

import com.my.financetracker.entity.User;
import com.my.financetracker.enums.Role;
import com.my.financetracker.models.requests.LoginRequest;
import com.my.financetracker.models.requests.RegisterRequest;
import com.my.financetracker.models.responses.AuthResponse;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.repository.UserRepository;
import com.my.financetracker.security.JwtService;
import com.my.financetracker.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public DefaultResponse<AuthResponse> register(@Valid RegisterRequest request) {
        log.info("Register request for email : {}", request.getEmail());

        try {
            if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
                return DefaultResponse.<AuthResponse>builder()
                        .code(ResponseUtil.DUPLICATE_RESOURCE)
                        .title(ResponseUtil.FAILED)
                        .message("Email already registered.")
                        .build();
            }

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(Role.USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);

            String token = jwtService.generateToken(savedUser.getEmail());

            AuthResponse response = AuthResponse.builder()
                    .userId(savedUser.getId())
                    .name(savedUser.getName())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole())
                    .token(token)
                    .build();

            return DefaultResponse.<AuthResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("User Registered successfully.")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Error while registering user : {}", e.getMessage());
            return DefaultResponse.<AuthResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to register user.")
                    .build();
        }
    }


    public DefaultResponse<AuthResponse> login(@Valid LoginRequest request) {
        log.info("Login request for email : {}", request.getEmail());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword() ));

            User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);
            if (user == null) {
                return DefaultResponse.<AuthResponse>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("User not found")
                        .data(null)
                        .build();
            }

            String token = jwtService.generateToken(user.getEmail());

            AuthResponse response = AuthResponse.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .token(token)
                    .build();

            return DefaultResponse.<AuthResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Login successful")
                    .data(response)
                    .build();

        } catch (BadCredentialsException e) {
            return DefaultResponse.<AuthResponse>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Invalid email or password")
                    .data(null)
                    .build();

        } catch (Exception e) {
            log.error("Error while login : {}", e.getMessage());
            return DefaultResponse.<AuthResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to login")
                    .data(null)
                    .build();
        }
    }
}
