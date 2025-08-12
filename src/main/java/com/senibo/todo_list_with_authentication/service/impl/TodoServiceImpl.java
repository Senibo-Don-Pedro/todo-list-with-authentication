package com.senibo.todo_list_with_authentication.service.impl;

import com.senibo.todo_list_with_authentication.model.Todo;
import com.senibo.todo_list_with_authentication.model.User;
import com.senibo.todo_list_with_authentication.repository.TodoRepository;
import com.senibo.todo_list_with_authentication.repository.UserRepository;
import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import com.senibo.todo_list_with_authentication.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Override
    public Todo createTodo(String title, String description, UserDetailsImpl currentUser) {
        User user = userRepository.findById(currentUser.getId())
                                  .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Todo todo = Todo.builder().title(title).description(description).user(user).build();

        return todoRepository.save(todo);
    }

    @Override
    public Page<Todo> getTodos(UserDetailsImpl currentUser, boolean isAdmin, Pageable pageable) {
        if (isAdmin) return todoRepository.findAll(pageable);
        return todoRepository.findByUserId(currentUser.getId(), pageable);
    }

    @Override
    public Todo updateTodo(Long id,
                           String title,
                           String description,
                           UserDetailsImpl currentUser,
                           boolean isAdmin) {
        Todo todo = todoRepository.findById(id)
                                  .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!isAdmin && !todo.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to update this todo");
        }

        todo.setTitle(title);
        todo.setDescription(description);
        return todoRepository.save(todo);
    }

    @Override
    public void deleteTodo(Long id, UserDetailsImpl currentUser, boolean isAdmin) {
        Todo todo = todoRepository.findById(id)
                                  .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!isAdmin && !todo.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this todo");
        }

        todoRepository.delete(todo);
    }
}
