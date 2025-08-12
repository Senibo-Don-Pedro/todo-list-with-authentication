package com.senibo.todo_list_with_authentication.controller;

import com.senibo.todo_list_with_authentication.dto.ApiResponse;
import com.senibo.todo_list_with_authentication.dto.auth.AuthResponse;
import com.senibo.todo_list_with_authentication.dto.auth.SigninRequest;
import com.senibo.todo_list_with_authentication.dto.auth.SignupRequest;
import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import com.senibo.todo_list_with_authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody SignupRequest req) {
        authService.register(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ApiResponse.success("User created successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody SigninRequest req) {
        AuthResponse res = authService.login(req);

        return ResponseEntity.ok(ApiResponse.success("Login successful", res));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(@AuthenticationPrincipal UserDetailsImpl me) {

        if(me == null) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("bearer token is required")
            );
        }
        return ResponseEntity.ok(
                ApiResponse.success(
                        "User profile retrieved successfully",
                        Map.of(
                                "id", me.getId(),
                                "username", me.getUsername(),
                                "email", me.getEmail(),
                                "roles", me.getAuthorities().stream().map(a -> a.getAuthority()).toList()
                        )
                )
        );
    }


}
