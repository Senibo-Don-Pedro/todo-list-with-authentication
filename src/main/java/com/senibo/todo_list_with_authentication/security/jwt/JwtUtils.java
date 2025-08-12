package com.senibo.todo_list_with_authentication.security.jwt;

import com.senibo.todo_list_with_authentication.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtUtils {


    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${spring.app.jwtSecret}")
    private String base64Secret;

    @Value("${spring.app.jwtExpirationMs}")
    private long expiresMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    public String generate(UserDetailsImpl user) {
        String roles = user.getAuthorities()
                           .stream()
                           .map(GrantedAuthority::getAuthority)
                           .collect(Collectors.joining(","));

        return Jwts.builder()
                   .subject(user.getUsername())
                   .claim("uid", user.getId())
                   .claim("roles", roles)
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + expiresMs))
                   .signWith(key())
                   .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                   .verifyWith(key())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());

        } catch (JwtException e) { // malformed/unsupported/signature, etc.
            log.warn("JWT invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT empty/illegal: {}", e.getMessage());
        }

        return false;
    }
}
