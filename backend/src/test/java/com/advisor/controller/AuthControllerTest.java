package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.entity.User;
import com.advisor.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void login_success() {
        // Given
        AuthController.LoginRequest request = new AuthController.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        String token = "mocked_jwt_token";
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRealName("Test User");

        when(authService.login(request.getUsername(), request.getPassword())).thenReturn(token);
        when(authService.getUserByUsername(request.getUsername())).thenReturn(user);

        // When
        Result<Map<String, Object>> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("登录成功", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(token, response.getData().get("token"));

        Map<String, Object> userData = (Map<String, Object>) response.getData().get("user");
        assertNotNull(userData);
        assertEquals(1L, userData.get("id"));
        assertEquals("testuser", userData.get("username"));
        assertEquals("test@example.com", userData.get("email"));
        assertEquals("Test User", userData.get("realName"));

        verify(authService, times(1)).login(request.getUsername(), request.getPassword());
        verify(authService, times(1)).getUserByUsername(request.getUsername());
    }

    @Test
    void login_failure_invalidCredentials() {
        // Given
        AuthController.LoginRequest request = new AuthController.LoginRequest();
        request.setUsername("wronguser");
        request.setPassword("wrongpassword");

        when(authService.login(anyString(), anyString())).thenThrow(new RuntimeException("Invalid credentials"));

        // When
        Result<Map<String, Object>> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getCode());
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getData());

        verify(authService, times(1)).login(anyString(), anyString());
        verify(authService, never()).getUserByUsername(anyString());
    }

    @Test
    void login_failure_userNotFoundAfterLogin() {
        // Given
        AuthController.LoginRequest request = new AuthController.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        String token = "mocked_jwt_token";

        when(authService.login(request.getUsername(), request.getPassword())).thenReturn(token);
        when(authService.getUserByUsername(request.getUsername())).thenReturn(null); // Simulate user not found

        // When
        Result<Map<String, Object>> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getCode());
        assertEquals("用户信息不存在", response.getMessage());
        assertNull(response.getData());

        verify(authService, times(1)).login(request.getUsername(), request.getPassword());
        verify(authService, times(1)).getUserByUsername(request.getUsername());
    }

    @Test
    void register_success() {
        // Given
        AuthController.RegisterRequest request = new AuthController.RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("newpassword");
        request.setEmail("new@example.com");
        request.setRealName("New User");

        doNothing().when(authService).register(anyString(), anyString(), anyString(), anyString());

        // When
        Result<Void> response = authController.register(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("注册成功", response.getMessage());
        assertNull(response.getData());

        verify(authService, times(1)).register(request.getUsername(), request.getPassword(), request.getEmail(), request.getRealName());
    }

    @Test
    void register_failure_usernameExists() {
        // Given
        AuthController.RegisterRequest request = new AuthController.RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password");
        request.setEmail("existing@example.com");
        request.setRealName("Existing User");

        doThrow(new RuntimeException("Username already exists")).when(authService).register(anyString(), anyString(), anyString(), anyString());

        // When
        Result<Void> response = authController.register(request);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getCode());
        assertEquals("Username already exists", response.getMessage());
        assertNull(response.getData());

        verify(authService, times(1)).register(request.getUsername(), request.getPassword(), request.getEmail(), request.getRealName());
    }

    @Test
    void logout_success() {
        // When
        Result<Void> response = authController.logout();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("登出成功", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void getCurrentUser_success() {
        // Given
        String authorizationHeader = "Bearer mocked_jwt_token";
        String token = "mocked_jwt_token";
        String username = "testuser";

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRealName("Test User");

        when(authService.getUsernameFromToken(token)).thenReturn(username);
        when(authService.getUserByUsername(username)).thenReturn(user);

        // When
        Result<Map<String, Object>> response = authController.getCurrentUser(authorizationHeader);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("操作成功", response.getMessage()); // Default success message
        assertNotNull(response.getData());

        Map<String, Object> userData = response.getData();
        assertEquals(1L, userData.get("id"));
        assertEquals("testuser", userData.get("username"));
        assertEquals("test@example.com", userData.get("email"));
        assertEquals("Test User", userData.get("realName"));

        verify(authService, times(1)).getUsernameFromToken(token);
        verify(authService, times(1)).getUserByUsername(username);
    }

    @Test
    void getCurrentUser_failure_invalidToken() {
        // Given
        String authorizationHeader = "Bearer invalid_token";

        when(authService.getUsernameFromToken(anyString())).thenThrow(new RuntimeException("Invalid JWT token"));

        // When
        Result<Map<String, Object>> response = authController.getCurrentUser(authorizationHeader);

        // Then
        assertNotNull(response);
        assertEquals(401, response.getCode());
        assertEquals("未授权访问", response.getMessage());
        assertNull(response.getData());

        verify(authService, times(1)).getUsernameFromToken(anyString());
        verify(authService, never()).getUserByUsername(anyString());
    }

    @Test
    void getCurrentUser_failure_userNotFound() {
        // Given
        String authorizationHeader = "Bearer valid_token";
        String token = "valid_token";
        String username = "nonexistentuser";

        when(authService.getUsernameFromToken(token)).thenReturn(username);
        when(authService.getUserByUsername(username)).thenReturn(null); // User not found

        // When
        Result<Map<String, Object>> response = authController.getCurrentUser(authorizationHeader);

        // Then
        assertNotNull(response);
        assertEquals(401, response.getCode());
        assertEquals("未授权访问", response.getMessage());
        assertNull(response.getData());

        verify(authService, times(1)).getUsernameFromToken(token);
        verify(authService, times(1)).getUserByUsername(username);
    }
}