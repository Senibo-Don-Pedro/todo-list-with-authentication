package com.senibo.todo_list_with_authentication.security;

import com.senibo.todo_list_with_authentication.security.jwt.AuthTokenFilter;
import com.senibo.todo_list_with_authentication.security.jwt.SecurityErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthTokenFilter jwtFilter;
    private final SecurityErrorHandler securityErrorHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                               .permitAll()
                                               .requestMatchers("/api/v1/auth/**")
                                               .permitAll()
                                               .requestMatchers("/v3/api-docs/**",
                                                                "/swagger-ui" + "/**",
                                                                "/swagger.html")
                                               .permitAll()
                                               //.requestMatchers("/api/admin/**").hasRole("ADMIN")
                                               .anyRequest()
                                               .authenticated());

        http.exceptionHandling(e -> e.authenticationEntryPoint(securityErrorHandler)
                                     .accessDeniedHandler(securityErrorHandler));
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
