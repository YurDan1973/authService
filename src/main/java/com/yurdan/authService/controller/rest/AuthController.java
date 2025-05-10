package com.yurdan.authService.controller.rest;

import com.yurdan.authService.model.LoginRequest;
import com.yurdan.authService.model.entity.BankUser;
import com.yurdan.authService.repository.BankUserRepository;
import com.yurdan.authService.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final BankUserRepository bankUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.login(loginRequest));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody BankUser bankUser) {
        if (bankUserRepository.findByEmail(bankUser.getEmail()) != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        bankUser.setPassword(passwordEncoder.encode(bankUser.getPassword()));
        BankUser savedUser = bankUserRepository.save(bankUser);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");

        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        Page<BankUser> users = bankUserRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }
}
