package com.yurdan.authService.controller.rest;

import com.yurdan.authService.model.LoginRequest;
import com.yurdan.authService.model.entity.BankUser;
import com.yurdan.authService.repository.BankUserRepository;
import com.yurdan.authService.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final BankUserRepository bankUserRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
//            );

//      // Получаем UserDetails для генерации JWT
//            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
//            String jwt = authService.generateToken(userDetails);
//            return ResponseEntity.ok(jwt);
            return ResponseEntity.ok(authService.login(loginRequest));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<BankUser> register(@RequestBody BankUser bankUser) {
        // Здесь можно добавить валидацию и проверку на существование пользователя
        BankUser savedUser = bankUserRepository.save(bankUser);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<BankUser>> getAllUsers() {
        List<BankUser> users = bankUserRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Можно добавить другие методы для управления пользователями
}
