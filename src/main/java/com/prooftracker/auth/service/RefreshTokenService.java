package com.prooftracker.auth.service;

import com.prooftracker.auth.entity.RefreshToken;
import com.prooftracker.auth.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}