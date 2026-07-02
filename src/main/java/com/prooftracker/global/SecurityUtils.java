package com.prooftracker.global;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserEmail() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}