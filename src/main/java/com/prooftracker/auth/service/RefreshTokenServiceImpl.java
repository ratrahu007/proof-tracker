package com.prooftracker.auth.service;

import com.prooftracker.auth.entity.RefreshToken;
import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.RefreshTokenRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Override
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository
                .findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusSeconds(
                                                refreshExpirationMs / 1000
                                        )
                        )
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(
            RefreshToken token
    ) {

        if (token.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(token);

            throw new AppException(
                    ErrorCode.INVALID_TOKEN,
                    "Refresh token expired"
            );
        }

        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(
            String token
    ) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}