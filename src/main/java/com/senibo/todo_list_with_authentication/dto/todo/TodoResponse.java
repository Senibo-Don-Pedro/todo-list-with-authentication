package com.senibo.todo_list_with_authentication.dto.todo;

public record TodoResponse(
        Long id,
        String title,
        String description,
        Long userId,
        String username
) {}
