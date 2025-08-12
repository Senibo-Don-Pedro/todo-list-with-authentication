package com.senibo.todo_list_with_authentication.controller;

import com.senibo.todo_list_with_authentication.dto.ApiResponse;
import com.senibo.todo_list_with_authentication.dto.todo.CreateTodoRequest;
import com.senibo.todo_list_with_authentication.dto.todo.TodoMapper;
import com.senibo.todo_list_with_authentication.dto.todo.TodoResponse;
import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import com.senibo.todo_list_with_authentication.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(@Valid @RequestBody CreateTodoRequest req,
                                                                @AuthenticationPrincipal
                                                     UserDetailsImpl currentUser) {

        var saved = todoService.createTodo(req.title(), req.description(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ApiResponse.success("Todo created successfully",
                                                       TodoMapper.toResponse(saved)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getTodos(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @org.springframework.data.web.PageableDefault(size = 10, sort = "id",
                    direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        boolean isAdmin = currentUser.getAuthorities().stream()
                                     .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var page = todoService.getTodos(currentUser, isAdmin, pageable)
                              .map(TodoMapper::toResponse);

        var body = Map.of(
                "content", page.getContent(),
                "page", page.getNumber(),
                "size", page.getSize(),
                "total", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "hasNext", page.hasNext(),
                "hasPrevious", page.hasPrevious()
        );
        return ResponseEntity.ok(ApiResponse.success("Todos retrieved successfully", body));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(@PathVariable Long id,
                                                     @Valid @RequestBody CreateTodoRequest req,
                                                     // or a separate UpdateTodoRequest
                                                     @AuthenticationPrincipal
                                                     UserDetailsImpl currentUser) {

        boolean isAdmin = currentUser.getAuthorities()
                                     .stream()
                                     .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var updated = todoService.updateTodo(id,
                                             req.title(),
                                             req.description(),
                                             currentUser,
                                             isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Todo updated successfully",
                                                     TodoMapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetailsImpl currentUser) {

        boolean isAdmin = currentUser.getAuthorities()
                                     .stream()
                                     .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        todoService.deleteTodo(id, currentUser, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
