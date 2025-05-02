package com.nt118.foodsellingapp.repository;

import com.nt118.foodsellingapp.entity.RefreshToken;
import com.nt118.foodsellingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
