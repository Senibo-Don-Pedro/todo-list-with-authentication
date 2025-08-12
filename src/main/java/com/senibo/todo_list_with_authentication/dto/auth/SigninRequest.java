package com.senibo.todo_list_with_authentication.dto.auth;
import jakarta.validation.constraints.NotBlank;


public record SigninRequest(
        @NotBlank(message = "User name or email is required")
        String identifier,

        @NotBlank(message = "Password is required")
        String password
) {
}
