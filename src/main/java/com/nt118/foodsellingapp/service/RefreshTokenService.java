package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.entity.RefreshToken;

public interface RefreshTokenService {
    public RefreshToken createRefreshToken(String email, String token, long refreshExpirationMs);

    public boolean validateRefreshToken(String token);

    public void deleteByUser(String email);
}
