package com.nt118.foodsellingapp.service.impl;

import com.nt118.foodsellingapp.repository.RefreshTokenRepository;
import com.nt118.foodsellingapp.repository.UserRepository;
import com.nt118.foodsellingapp.entity.RefreshToken;
import com.nt118.foodsellingapp.entity.User;
import com.nt118.foodsellingapp.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RefreshToken createRefreshToken(String email, String token, long refreshExpirationMs) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiredAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(t -> t.getExpiredAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void deleteByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        refreshTokenRepository.deleteByUser(user);
    }
}
