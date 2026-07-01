package com.prooftracker.auth.controller;

import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/jwt")
    public String testJwt() {

        User user = userRepository
                .findByEmail("rahulrathod6624@gmail.com")
                .orElseThrow();

        return jwtService.generateToken(user);
    }

    @GetMapping("/secure")
    public String secure() {
        return "JWT Working";
    }
}
