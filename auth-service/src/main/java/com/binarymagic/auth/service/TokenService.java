package com.binarymagic.auth.service;

import com.binarymagic.auth.dto.AuthDtos.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Orchestrates token issuance.
 *
 * Currently: anonymous only.
 * Future:    add issueAuthenticatedToken(userId, email) here
 *            when login is implemented — no other class changes.
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final JwtService jwtService;

    public TokenService() {
        // Default constructor for testing
        this.jwtService = null;
    }

    public TokenService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public TokenResponse issueAnonymousToken() {
        String sessionId = UUID.randomUUID().toString();
        String token     = jwtService.generateAnonymousToken(sessionId);
        long   expiresIn = jwtService.getTtlSeconds();

        log.info("Issued anonymous token for sessionId={}", sessionId);

        return new TokenResponse(token, sessionId, expiresIn);
    }
}
