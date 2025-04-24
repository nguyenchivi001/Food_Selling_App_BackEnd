package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.AuthRequest;
import com.nt118.foodsellingapp.dto.AuthResponse;
import com.nt118.foodsellingapp.dto.RefreshTokenRequest;
import com.nt118.foodsellingapp.security.JwtService;
import com.nt118.foodsellingapp.service.CustomUserDetailsService;
import com.nt118.foodsellingapp.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // milliseconds

    // =====================
    // 1. LOGIN
    // =====================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.createRefreshToken(request.getEmail(), refreshToken, refreshExpiration);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    // =====================
    // 2. REFRESH TOKEN
    // =====================
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String email = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            boolean isValid = jwtService.isTokenValid(refreshToken, userDetails)
                    && refreshTokenService.validateRefreshToken(refreshToken);

            if (isValid) {
                String newAccessToken = jwtService.generateToken(userDetails);
                return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
            }

        } catch (Exception e) {
            // log.error("Invalid refresh token: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // =====================
    // 3. LOGOUT
    // =====================
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody AuthRequest request) {
        refreshTokenService.deleteByUser(request.getEmail());
        return ResponseEntity.ok("Logged out successfully");
    }
}
