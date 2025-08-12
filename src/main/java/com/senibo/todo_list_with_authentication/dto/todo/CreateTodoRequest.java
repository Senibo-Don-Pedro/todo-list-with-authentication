package com.senibo.todo_list_with_authentication.dto.todo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Create/update todo payload.")
public record CreateTodoRequest(

        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters long")
        @Schema(example = "Buy milk")
        String title,

        @NotBlank(message = "Description is required")
        @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters long")
        @Schema(example = "2 liters")
        String description
) {
}
