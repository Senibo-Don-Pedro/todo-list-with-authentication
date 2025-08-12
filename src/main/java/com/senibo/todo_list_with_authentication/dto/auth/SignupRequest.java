package com.senibo.todo_list_with_authentication.dto.auth;

import com.senibo.todo_list_with_authentication.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration payload.")
public record SignupRequest(
        @NotBlank(message = "Username is required")
        @Schema(example = "jane")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@example\\.com$",
                message = "Email must be in the example.com domain"
        )
        @Schema(example = "jane@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
                // at least one digit, one lower, one upper, one special
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "Password must contain at least one digit, one lower, one upper, one special character"
        )
        @Schema(example = "P@ssw0rd!")
        String password
) {
}
