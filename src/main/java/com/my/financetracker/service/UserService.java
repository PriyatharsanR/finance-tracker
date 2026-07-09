package com.my.financetracker.service;

import com.my.financetracker.entity.User;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.models.responses.UserResponse;
import com.my.financetracker.repository.UserRepository;
import com.my.financetracker.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public DefaultResponse<UserResponse> getCurrentUserProfile() {

        log.info("Fetching current user profile");

        try {
            User user = getCurrentUser();

            return DefaultResponse.<UserResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("User profile fetched successfully")
                    .data(mapToUserResponse(user))
                    .build();

        } catch (Exception e) {
            log.error("Error while fetching user profile", e);

            return DefaultResponse.<UserResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to fetch user profile")
                    .data(null)
                    .build();
        }
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
    }

    private UserResponse mapToUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}