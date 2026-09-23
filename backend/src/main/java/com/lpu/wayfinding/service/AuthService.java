// src/main/java/com/lpu/wayfinding/service/AuthService.java
package com.lpu.wayfinding.service;

import com.lpu.wayfinding.dto.request.LoginRequest;
import com.lpu.wayfinding.dto.request.RegisterRequest;
import com.lpu.wayfinding.dto.response.AuthResponse;
import com.lpu.wayfinding.entity.User;
import com.lpu.wayfinding.exception.WayfindingException;
import com.lpu.wayfinding.repository.UserRepository;
import com.lpu.wayfinding.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new WayfindingException("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new WayfindingException("Email already registered");
        }
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .enabled(true)
                .build();
        userRepository.save(user);

        UserDetails details = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(details);
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .expiresIn(jwtService.getExpirationSeconds())
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new WayfindingException("User not found"));

        UserDetails details = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(details);
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .expiresIn(jwtService.getExpirationSeconds())
                .build();
    }
}