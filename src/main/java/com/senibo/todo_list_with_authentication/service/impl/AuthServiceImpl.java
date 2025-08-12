package com.senibo.todo_list_with_authentication.service.impl;

import com.senibo.todo_list_with_authentication.dto.auth.AuthResponse;
import com.senibo.todo_list_with_authentication.dto.auth.SigninRequest;
import com.senibo.todo_list_with_authentication.dto.auth.SignupRequest;
import com.senibo.todo_list_with_authentication.model.Role;
import com.senibo.todo_list_with_authentication.model.User;
import com.senibo.todo_list_with_authentication.repository.UserRepository;
import com.senibo.todo_list_with_authentication.security.jwt.JwtUtils;
import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import com.senibo.todo_list_with_authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository users;
    private final JwtUtils jwt;

    @Transactional
    @Override
    public void register(SignupRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username taken");
        }
        if (users.existsByEmail(req.email())) {
            throw new IllegalArgumentException(String.format("Email %s in use", req.email()));
        }

        var user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(Role.ROLE_USER)
                .build();

        users.save(user);

        log.info("Signup success userId={} username={}", user.getId(), user.getUsername());
    }

    @Override
    public AuthResponse login(SigninRequest req) {
        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.identifier(), req.password()));
        var principal = (UserDetailsImpl) auth.getPrincipal();
        var token = jwt.generate(principal);
        var roles = principal.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        log.info("Login success user={} roles={}", principal.getUsername(), roles);
        return new AuthResponse(token, principal.getUsername(), roles);
    }



}
