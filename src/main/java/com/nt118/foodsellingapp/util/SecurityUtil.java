package com.nt118.foodsellingapp.util;

import com.nt118.foodsellingapp.entity.User;
import com.nt118.foodsellingapp.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;

public class SecurityUtil {
    public static int extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }

        throw new ResourceNotFoundException("Invalid user info");
    }
}
