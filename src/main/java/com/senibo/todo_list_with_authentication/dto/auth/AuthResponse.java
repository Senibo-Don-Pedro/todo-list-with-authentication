package com.senibo.todo_list_with_authentication.dto.auth;

import java.util.List;

public record AuthResponse(
        String token,
        String username,
        List<String> roles
) {
}
