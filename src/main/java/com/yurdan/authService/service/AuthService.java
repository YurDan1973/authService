package com.yurdan.authService.service;

import com.yurdan.authService.model.LoginRequest;
import com.yurdan.authService.model.entity.BankUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(BankUser bankUser) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, bankUser.getPassword());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 часов
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String login(LoginRequest loginRequest) {
        return null;
    }
}

