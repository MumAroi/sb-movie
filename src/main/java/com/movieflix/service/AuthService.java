package com.movieflix.service;

import com.movieflix.auth.entities.User;
import com.movieflix.auth.entities.UserRole;
import com.movieflix.auth.repositories.UserRepository;
import com.movieflix.auth.service.JwtService;
import com.movieflix.auth.service.RefreshTokenService;
import com.movieflix.auth.utils.AuthResponse;
import com.movieflix.auth.utils.LoginRequest;
import com.movieflix.auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
            .name(registerRequest.getName())
            .email(registerRequest.getEmail())
            .username(registerRequest.getUsername())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .role(UserRole.USER)
            .build();

        User saveUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(saveUser);
        var refreshToken = refreshTokenService.createRefreshToken(
            saveUser.getEmail()
        );

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getRefreshToken())
            .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        var user = userRepository
            .findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found!")
            );

        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(
            loginRequest.getUsername()
        );

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getRefreshToken())
            .build();
    }
}
