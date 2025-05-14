package com.yurdan.authService.controller.rest;

import com.yurdan.authService.model.LoginRequest;
import com.yurdan.authService.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerLoginTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(authService.login(loginRequest)).thenReturn("mocked-jwt-token");

        ResponseEntity<String> response = authController.login(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("mocked-jwt-token", response.getBody());
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void testLoginFailure() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrong");

        when(authService.login(loginRequest)).thenThrow(new RuntimeException("Invalid email or password"));

        ResponseEntity<String> response = authController.login(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid email or password", response.getBody());
        verify(authService, times(1)).login(loginRequest);
    }
}

