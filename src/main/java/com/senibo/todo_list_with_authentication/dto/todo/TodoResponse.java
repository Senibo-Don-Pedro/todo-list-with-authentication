package com.senibo.todo_list_with_authentication.dto.todo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Todo resource.")
public record TodoResponse(
        @Schema(example = "10") Long id,
        @Schema(example = "Buy milk") String title,
        @Schema(example = "2 liters") String description,
        @Schema(example = "1") Long userId,
        @Schema(example = "jane") String username
) {}
