package com.prooftracker.auth.service;

import com.prooftracker.auth.dto.*;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String token);

    void logout(String token);

}
