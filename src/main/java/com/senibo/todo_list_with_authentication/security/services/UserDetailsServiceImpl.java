package com.senibo.todo_list_with_authentication.security.services;

import com.senibo.todo_list_with_authentication.model.User;
import com.senibo.todo_list_with_authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository users;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Accept username OR email
        User user = users.findByUsername(usernameOrEmail)
                .or(() -> users.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        return UserDetailsImpl.from(user);
    }
}
