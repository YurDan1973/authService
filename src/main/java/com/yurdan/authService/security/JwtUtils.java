package com.yurdan.authService.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return (String) getPayload(token).get("email");
    }

    public List<String> getRolesFromToken(String token) {
        Object rolesObj = getPayload(token).get("roles");
        if (rolesObj instanceof List<?> roles) {
            return roles.stream()
                    .map(Object::toString)
                    .toList();
        }
        return Collections.emptyList();
    }

    private Map<String, Object> getPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid token");

        String payloadJson = new String(Base64.getDecoder().decode(parts[1]));
        try {
            return new ObjectMapper().readValue(payloadJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Invalid token payload", e);
        }
    }
}

