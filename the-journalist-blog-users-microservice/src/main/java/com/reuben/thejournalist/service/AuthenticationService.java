package com.reuben.thejournalist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reuben.thejournalist.auth.AuthenticationResponse;
import com.reuben.thejournalist.config.JwtService;
import com.reuben.thejournalist.model.Token;
import com.reuben.thejournalist.model.TokenType;
import com.reuben.thejournalist.model.UserEntity;
import com.reuben.thejournalist.repository.TokenRepository;
import com.reuben.thejournalist.repository.UserEntityRepository;
import com.reuben.thejournalist.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserEntityRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;



    public void saveUserToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(UserEntity user) {
        System.out.println("INSIDE REVOKE ALL USER TOKENS: " + user.getIdAsString());
        var validUserTokens = tokenRepository.findValidTokenByUser(user.get_id());
        if (validUserTokens.isEmpty())
            return;
        System.out.println("VALID USER TOKENS: " + validUserTokens);

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.save(token);
        });

    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractEmail(refreshToken);
        System.out.println("USER EMAIL: " + userEmail);
        if (userEmail != null) {
            var user = this.repository.findUserByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user,user);
//                revokeAllUserTokens(user);
//                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}