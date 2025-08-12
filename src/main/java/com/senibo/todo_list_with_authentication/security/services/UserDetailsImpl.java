package com.senibo.todo_list_with_authentication.security.services;

import com.senibo.todo_list_with_authentication.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl from(User u) {
        return new UserDetailsImpl(
                u.getId(), u.getUsername(), u.getEmail(), u.getPassword(),
                List.of(new SimpleGrantedAuthority(u.getRole().name()))
        );
    }

    public boolean isAccountNonExpired(){ return true; }
    public boolean isAccountNonLocked(){ return true; }
    public boolean isCredentialsNonExpired(){ return true; }
    public boolean isEnabled(){ return true; }
}
