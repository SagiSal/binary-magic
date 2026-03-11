package com.binarymagic.auth.controller;

import com.binarymagic.auth.dto.AuthDtos.TokenResponse;
import com.binarymagic.auth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;

    @Autowired
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * POST /api/auth/token
     *
     * Issues an anonymous JWT. No body required.
     * The returned token identifies this browser session for 24h.
     *
     * Future: POST /api/auth/register and POST /api/auth/login
     * will live here when full login is added.
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken() {
        return ResponseEntity.ok(tokenService.issueAnonymousToken());
    }
}
