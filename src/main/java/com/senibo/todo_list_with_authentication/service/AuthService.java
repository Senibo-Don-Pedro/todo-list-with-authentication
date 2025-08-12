package com.senibo.todo_list_with_authentication.service;

import com.senibo.todo_list_with_authentication.dto.auth.AuthResponse;
import com.senibo.todo_list_with_authentication.dto.auth.SigninRequest;
import com.senibo.todo_list_with_authentication.dto.auth.SignupRequest;

// AuthService.java
public interface AuthService {
  void register(SignupRequest req);
  AuthResponse login(SigninRequest req);
}
