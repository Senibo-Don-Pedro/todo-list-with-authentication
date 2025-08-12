package com.senibo.todo_list_with_authentication.service;

import com.senibo.todo_list_with_authentication.model.Todo;
import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoService {

    Todo createTodo(String title, String description, UserDetailsImpl currentUser);

    Page<Todo> getTodos(UserDetailsImpl currentUser, boolean isAdmin, Pageable pageable);

    Todo updateTodo(Long id,
                    String title,
                    String description,
                    UserDetailsImpl currentUser,
                    boolean isAdmin);

    void deleteTodo(Long id, UserDetailsImpl currentUser, boolean isAdmin);
}
