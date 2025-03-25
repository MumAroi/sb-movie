package com.movieflix.controllers;

import com.movieflix.auth.entities.RefreshToken;
import com.movieflix.auth.entities.User;
import com.movieflix.auth.service.JwtService;
import com.movieflix.auth.service.RefreshTokenService;
import com.movieflix.auth.utils.AuthResponse;
import com.movieflix.auth.utils.LoginRequest;
import com.movieflix.auth.utils.RefreshTokenRequest;
import com.movieflix.auth.utils.RegisterRequest;
import com.movieflix.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RequestBody LoginRequest loRequest
    ) {
        return ResponseEntity.ok(authService.login(loRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());

        User user = refreshToken.getUser();

        String accressToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accressToken)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .build());
    }
}
