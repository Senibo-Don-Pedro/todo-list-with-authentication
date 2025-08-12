package com.senibo.todo_list_with_authentication.controller;

import com.senibo.todo_list_with_authentication.dto.ApiResponse;
import com.senibo.todo_list_with_authentication.dto.todo.CreateTodoRequest;
import com.senibo.todo_list_with_authentication.dto.todo.TodoMapper;
import com.senibo.todo_list_with_authentication.dto.todo.TodoResponse;
import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import com.senibo.todo_list_with_authentication.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Operation(summary = "Create todo", description = "Add a new todo for the current user.")
    @ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Todo created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "CreateTodoSuccess", value = """
            {
              "success": true,
              "message": "Todo created successfully",
              "data": {
                "id": 10,
                "title": "Buy milk",
                "description": "2 liters",
                "userId": 1,
                "username": "jane"
              },
              "error": null,
              "errors": null
            }
            """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "CreateTodoValidationError", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "Validation failed for the following reasons:",
              "errors": [
                "title: Title is required",
                "description: Description must be between 3 and 255 characters long"
              ]
            }
            """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)")})
    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
            @Valid @RequestBody CreateTodoRequest req,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        var saved = todoService.createTodo(req.title(), req.description(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ApiResponse.success("Todo created successfully",
                                                       TodoMapper.toResponse(saved)));
    }

    @Operation(summary = "List todos", description = "Paginated list; admins see all, users see theirs.")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getTodos(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {

        boolean isAdmin = currentUser.getAuthorities()
                                     .stream()
                                     .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var page = todoService.getTodos(currentUser, isAdmin, pageable).map(TodoMapper::toResponse);

        var body = Map.of("content",
                          page.getContent(),
                          "page",
                          page.getNumber(),
                          "size",
                          page.getSize(),
                          "total",
                          page.getTotalElements(),
                          "totalPages",
                          page.getTotalPages(),
                          "hasNext",
                          page.hasNext(),
                          "hasPrevious",
                          page.hasPrevious());
        return ResponseEntity.ok(ApiResponse.success("Todos retrieved successfully", body));
    }


    @Operation(summary = "Update todo", description = "Update title/description by ID.")
    @ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Todo updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "UpdateTodoSuccess", value = """
            {
              "success": true,
              "message": "Todo updated successfully",
              "data": {
                "id": 10,
                "title": "Buy milk (almond)",
                "description": "2 liters",
                "userId": 1,
                "username": "jane"
              },
              "error": null,
              "errors": null
            }
            """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (not owner and not admin)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "UpdateForbidden", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "Access is denied",
              "errors": null
            }
            """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Todo not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "TodoNotFound", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "Todo not found",
              "errors": null
            }
            """)))})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(@PathVariable Long id,
                                                                @Valid @RequestBody
                                                                CreateTodoRequest req,
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

    @Operation(summary = "Delete todo", description = "Delete by ID.")
    @ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Deleted (no content)"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "DeleteForbidden", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "Access is denied",
              "errors": null
            }
            """))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "DeleteNotFound", value = """
            {
              "success": false,
              "message": null,
              "data": null,
              "error": "Todo not found",
              "errors": null
            }
            """)))})
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
