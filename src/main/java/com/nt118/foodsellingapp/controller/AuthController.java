package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dao.UserRepository;
import com.nt118.foodsellingapp.dto.AuthRequest;
import com.nt118.foodsellingapp.dto.AuthResponse;
import com.nt118.foodsellingapp.dto.RefreshTokenRequest;
import com.nt118.foodsellingapp.dto.RegisterRequest;
import com.nt118.foodsellingapp.entity.User;
import com.nt118.foodsellingapp.exception.BadRequestException;
import com.nt118.foodsellingapp.security.JwtService;
import com.nt118.foodsellingapp.service.CustomUserDetailsService;
import com.nt118.foodsellingapp.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // 1. LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.error("Login failed for user: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.deleteByUser(request.getEmail());
        refreshTokenService.createRefreshToken(request.getEmail(), refreshToken, refreshExpiration);

        return ResponseEntity.ok(new AuthResponse(token, refreshToken));
    }

    // 2. REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        String refreshToken = request.getRefreshToken();

        try {
            String email = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            boolean isValid = jwtService.isTokenValid(refreshToken, userDetails)
                    && refreshTokenService.validateRefreshToken(refreshToken);

            if (isValid) {
                String newToken = jwtService.generateToken(userDetails);
                return ResponseEntity.ok(new AuthResponse(newToken, refreshToken));
            }

        } catch (Exception e) {
            log.error("Invalid refresh token: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Invalid refresh token");
    }

    // 3. LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody AuthRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        refreshTokenService.deleteByUser(request.getEmail());
        return ResponseEntity.ok("Logged out successfully");
    }

    // 4. REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole("USER");

        try {
            userRepository.save(user);
            return ResponseEntity.ok("Register successful");
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed. Please try again later.");
        }
    }
}
