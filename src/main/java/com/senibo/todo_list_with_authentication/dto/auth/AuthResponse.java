package com.senibo.todo_list_with_authentication.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "JWT + user info.")
public record AuthResponse(
        @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(example = "jane")
        String username,

        @Schema(example = "[\"ROLE_USER\"]")
        List<String> roles
) {
}
