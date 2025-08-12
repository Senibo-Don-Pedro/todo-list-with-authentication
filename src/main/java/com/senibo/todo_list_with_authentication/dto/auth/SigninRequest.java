package com.senibo.todo_list_with_authentication.dto.auth;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "Login payload (email/username + password).")
public record SigninRequest(
        @NotBlank(message = "User name or email is required")
        @Schema(example = "jane@example.com")
        String identifier,

        @NotBlank(message = "Password is required")
        @Schema(example = "P@ssw0rd!")
        String password
) {
}
