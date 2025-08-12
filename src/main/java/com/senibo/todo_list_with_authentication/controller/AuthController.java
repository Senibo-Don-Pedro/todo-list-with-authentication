package com.senibo.todo_list_with_authentication.controller;

import com.senibo.todo_list_with_authentication.dto.ApiResponse;
import com.senibo.todo_list_with_authentication.dto.auth.AuthResponse;
import com.senibo.todo_list_with_authentication.dto.auth.SigninRequest;
import com.senibo.todo_list_with_authentication.dto.auth.SignupRequest;
import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import com.senibo.todo_list_with_authentication.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Signup", description = "Create a new user account.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "SignupSuccess", value = """
            {
              "success": true,
              "message": "User created successfully",
              "data": null,
              "error": null,
              "errors": null
            }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "SignupValidationError", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "Validation failed for the following reasons:",
              "errors": [
                "username: Username is required",
                "email: Email must be in the example.com domain",
                "password: Password must be at least 8 characters long"
              ]
            }
            """)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody SignupRequest req) {
        authService.register(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ApiResponse.success("User created successfully", null));
    }

    @Operation(summary = "Login", description = "Authenticate and receive a JWT.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "LoginSuccess", value = """
            {
              "success": true,
              "message": "Login successful",
              "data": {
                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                "username": "jane",
                "roles": ["ROLE_USER"]
              },
              "error": null,
              "errors": null
            }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "LoginUnauthorized", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "Invalid credentials",
              "errors": null
            }
            """)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody SigninRequest req) {
        AuthResponse res = authService.login(req);

        return ResponseEntity.ok(ApiResponse.success("Login successful", res));
    }

    @Operation(summary = "Me", description = "Get the current user's profile (requires Bearer token).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "MeSuccess", value = """
            {
              "success": true,
              "message": "User profile retrieved successfully",
              "data": {
                "id": 1,
                "username": "jane",
                "email": "jane@example.com",
                "roles": ["ROLE_USER"]
              },
              "error": null,
              "errors": null
            }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Missing/invalid token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(name = "MeMissingToken", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "bearer token is required",
              "errors": null
            }
            """)))
    })
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
