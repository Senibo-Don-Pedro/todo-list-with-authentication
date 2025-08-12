// src/main/java/com/example/auth/security/AuthTokenFilter.java
package com.senibo.todo_list_with_authentication.security.jwt;

import com.senibo.todo_list_with_authentication.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwt;
    private final UserDetailsServiceImpl uds;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {

            log.debug("Bearer token present on {}", req.getRequestURI());

            String token = auth.substring(7);

            if (jwt.isValid(token)) {
                String username = jwt.getUsername(token);
                var userDetails = uds.loadUserByUsername(username);
                var authToken = new UsernamePasswordAuthenticationToken(userDetails,
                                                                        null,
                                                                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(req, res);
    }
}
