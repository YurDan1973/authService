//package com.yurdan.authService.controller.rest;
//
//import com.yurdan.authService.model.LoginRequest;
//import com.yurdan.authService.model.entity.BankUser;
//import com.yurdan.authService.repository.BankUserRepository;
//import com.yurdan.authService.service.AuthService;
//import io.jsonwebtoken.*;
//import lombok.AllArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/auth")
//@AllArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//    private final BankUserRepository bankUserRepository;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
//        try {
//            return ResponseEntity.ok(authService.login(loginRequest));
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Invalid email or password");
//        }
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
//        // Здесь можно добавить токен в "чёрный список"
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
//        }
//
//        // В продакшене желательно использовать Redis/БД для хранения невалидных токенов
//        return ResponseEntity.ok("Logged out successfully");
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody BankUser bankUser) {
//        if (bankUserRepository.findByEmail(bankUser.getEmail()) != null) {
//            return ResponseEntity.badRequest().body("User already exists");
//        }
//
//        bankUser.setPassword(passwordEncoder.encode(bankUser.getPassword()));
//        BankUser savedUser = bankUserRepository.save(bankUser);
//        return ResponseEntity.ok(savedUser);
//    }
//
//    @GetMapping("/users")
//    public ResponseEntity<?> getAllUsers(
//            @RequestHeader("Authorization") String authHeader,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
//        }
//
//        String token = authHeader.replace("Bearer ", "");
//
//        if (!authService.isAdmin(token)) {
//            return ResponseEntity.status(403).body("Access denied");
//        }
//
//        Page<BankUser> users = bankUserRepository.findAll(PageRequest.of(page, size));
//        return ResponseEntity.ok(users);
//    }
//
//}

package com.yurdan.authService.controller.rest;

import com.yurdan.authService.model.LoginRequest;
import com.yurdan.authService.model.dto.RegisterDto;
import com.yurdan.authService.model.entity.BankUser;
import com.yurdan.authService.model.entity.Role;
import com.yurdan.authService.repository.BankUserRepository;
import com.yurdan.authService.service.AuthService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
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

//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody BankUser bankUser) {
//        if (bankUserRepository.findByEmail(bankUser.getEmail()) != null) {
//            return ResponseEntity.badRequest().body("User already exists");
//        }
//
//        bankUser.setPassword(passwordEncoder.encode(bankUser.getPassword()));
//        BankUser savedUser = bankUserRepository.save(bankUser);
//        return ResponseEntity.ok(savedUser);
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        if (bankUserRepository.findByEmail(registerDto.email()) != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        BankUser bankUser = new BankUser();
        bankUser.setEmail(registerDto.email());
        bankUser.setPassword(passwordEncoder.encode(registerDto.password()));
//        bankUser.setRoles(List.of(new Role(1L, Role.RoleName.USER))); // Добавлялся только USER
        bankUser.setRoles(List.of(new Role(1L, Role.RoleName.USER), new Role(2L, Role.RoleName.ADMIN)));
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

    // 🔍 Валидация токена (без Redis)
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(authService.getSecret().getBytes())
                    .parseClaimsJws(token);

            return ResponseEntity.ok(claims.getBody());
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body("Token expired");
        } catch (JwtException e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
}

