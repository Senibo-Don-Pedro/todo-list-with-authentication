package com.senibo.todo_list_with_authentication.dto.todo;

import com.senibo.todo_list_with_authentication.model.Todo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TodoMapper {

    public static TodoResponse toResponse(Todo t) {
        return new TodoResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getUser().getId(),
                t.getUser().getUsername()
        );
    }
}
