package com.prooftracker.auth.service;

import com.prooftracker.auth.dto.LoginRequest;
import com.prooftracker.auth.dto.RegisterRequest;
import com.prooftracker.auth.dto.AuthResponse;
import com.prooftracker.auth.entity.RefreshToken;
import com.prooftracker.auth.security.JwtService;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.notification.enums.NotificationChannel;
import com.prooftracker.otp.entity.OtpVerification;
import com.prooftracker.otp.repository.OtpRepository;
import com.prooftracker.otp.service.OtpService;
import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.enums.Role;
import com.prooftracker.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Email already registered"
            );
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                null,
                null
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND,
                                "User not found"
                        )
                );

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new AppException(
                    ErrorCode.ACCOUNT_DISABLED,
                    "Account is disabled"
            );
        }

        OtpVerification otpVerification =
                otpRepository
                        .findTopByRecipientAndChannelOrderByCreatedAtDesc(
                                request.email(),
                                NotificationChannel.EMAIL
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.EMAIL_NOT_VERIFIED,
                                        "Please verify your email first"
                                )
                        );

        if (!Boolean.TRUE.equals(
                otpVerification.getVerified())) {

            throw new AppException(
                    ErrorCode.EMAIL_NOT_VERIFIED,
                    "Please verify your email first"
            );
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {

            throw new AppException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid email or password"
            );
        }

        String accessToken =
                jwtService.generateToken(user);

        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken.getToken()
        );
    }
}