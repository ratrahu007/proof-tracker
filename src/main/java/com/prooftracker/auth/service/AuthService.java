package com.prooftracker.auth.service;

import com.prooftracker.auth.dto.AuthResponse;
import com.prooftracker.auth.dto.LoginRequest;
import com.prooftracker.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
