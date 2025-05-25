package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.request.AuthRequest;
import com.nt118.foodsellingapp.dto.request.LogoutRequest;
import com.nt118.foodsellingapp.dto.request.RefreshTokenRequest;
import com.nt118.foodsellingapp.dto.request.RegisterRequest;
import com.nt118.foodsellingapp.dto.response.AuthResponse;
import com.nt118.foodsellingapp.entity.User;
import com.nt118.foodsellingapp.repository.UserRepository;
import com.nt118.foodsellingapp.security.JwtService;
import com.nt118.foodsellingapp.service.CustomUserDetailsService;
import com.nt118.foodsellingapp.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AuthController authController;

    private AuthRequest authRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private LogoutRequest logoutRequest;
    private RegisterRequest registerRequest;
    private UserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("refresh-token");

        logoutRequest = new LogoutRequest();
        logoutRequest.setEmail("test@example.com");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setName("Test User");

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded-password");
        user.setName("Test User");
        user.setRole("USER");

        userDetails = mock(UserDetails.class);
    }

    // Login Tests
    @Test
    void login_ValidCredentials_ReturnsAuthResponse() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(authRequest.getEmail())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(customUserDetailsService.loadUserByUsername(authRequest.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(refreshTokenService.createRefreshToken(anyString(), anyString(), anyLong()))
                .thenReturn(mock(com.nt118.foodsellingapp.entity.RefreshToken.class));

        ResponseEntity<?> response = authController.login(authRequest, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("jwt-token", authResponse.getToken());
        assertEquals("refresh-token", authResponse.getRefreshToken());
        verify(refreshTokenService).deleteByUser(eq(authRequest.getEmail()));
        verify(refreshTokenService).createRefreshToken(eq(authRequest.getEmail()), eq("refresh-token"), anyLong());
    }

    @Test
    void login_InvalidEmail_ReturnsNotFound() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(authRequest.getEmail())).thenReturn(false);

        ResponseEntity<?> response = authController.login(authRequest, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Email không tồn tại", response.getBody());
        verify(userRepository).existsByEmail(authRequest.getEmail());
        verifyNoInteractions(authenticationManager, customUserDetailsService, jwtService, refreshTokenService);
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(authRequest.getEmail())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authController.login(authRequest, bindingResult);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Sai thông tin đăng nhập", response.getBody());
        verify(userRepository).existsByEmail(authRequest.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(customUserDetailsService, jwtService, refreshTokenService);
    }

    @Test
    void login_ValidationErrors_ReturnsBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = authController.login(authRequest, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(userRepository, authenticationManager, customUserDetailsService, jwtService, refreshTokenService);
    }

    // Refresh Token Tests
    @Test
    void refreshToken_ValidToken_ReturnsNewAuthResponse() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(jwtService.extractUsername(refreshTokenRequest.getRefreshToken())).thenReturn("test@example.com");
        when(customUserDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), userDetails)).thenReturn(true);
        when(refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken())).thenReturn(true);
        when(jwtService.generateToken(userDetails)).thenReturn("new-jwt-token");

        ResponseEntity<?> response = authController.refreshToken(refreshTokenRequest, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("new-jwt-token", authResponse.getToken());
        assertEquals("refresh-token", authResponse.getRefreshToken());
        verify(jwtService).extractUsername("refresh-token");
        verify(customUserDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).isTokenValid("refresh-token", userDetails);
        verify(refreshTokenService).validateRefreshToken("refresh-token");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void refreshToken_InvalidToken_ReturnsForbidden() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(jwtService.extractUsername(refreshTokenRequest.getRefreshToken())).thenReturn("test@example.com");
        when(customUserDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), userDetails)).thenReturn(false);

        ResponseEntity<?> response = authController.refreshToken(refreshTokenRequest, bindingResult);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Invalid refresh token", response.getBody());
        verify(jwtService).extractUsername("refresh-token");
        verify(customUserDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).isTokenValid("refresh-token", userDetails);
        verify(refreshTokenService, never()).validateRefreshToken(anyString());
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void refreshToken_ValidationErrors_ReturnsBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = authController.refreshToken(refreshTokenRequest, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(jwtService, customUserDetailsService, refreshTokenService);
    }

    // Logout Tests
    @Test
    void logout_ValidRequest_ReturnsSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = authController.logout(logoutRequest, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Đăng xuất thành công", response.getBody());
        verify(refreshTokenService).deleteByUser(logoutRequest.getEmail());
    }

    @Test
    void logout_ValidationErrors_ReturnsBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = authController.logout(logoutRequest, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(refreshTokenService);
    }

    // Register Tests
    @Test
    void register_ValidRequest_ReturnsSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = authController.register(registerRequest, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Register successful", response.getBody());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_EmailExists_ReturnsConflict() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.register(registerRequest, bindingResult);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email đã tồn tại", response.getBody());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ValidationErrors_ReturnsBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = authController.register(registerRequest, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void register_DatabaseError_ReturnsInternalServerError() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = authController.register(registerRequest, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Đăng nhập thất bại.", response.getBody());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }
}