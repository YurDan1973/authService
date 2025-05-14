package com.yurdan.authService.controller.rest;

import com.yurdan.authService.model.entity.BankUser;
import com.yurdan.authService.repository.BankUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerRegisterTest {

    @Mock
    private BankUserRepository bankUserRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private BankUser bankUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bankUser = new BankUser();
        bankUser.setEmail("test@example.com");
        bankUser.setPassword("plaintext-password");
    }

    @Test
    void testRegisterSuccess() {
        when(bankUserRepository.findByEmail(bankUser.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(bankUser.getPassword())).thenReturn("hashed-password");
        when(bankUserRepository.save(any(BankUser.class))).thenReturn(bankUser);

        ResponseEntity<?> response = authController.register(bankUser);

        assertEquals(200, response.getStatusCodeValue());
        verify(bankUserRepository, times(1)).save(any(BankUser.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(bankUserRepository.findByEmail(bankUser.getEmail())).thenReturn(bankUser);

        ResponseEntity<?> response = authController.register(bankUser);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody());
        verify(bankUserRepository, never()).save(any());
    }
}

