package com.binarymagic.auth.dto;

public final class AuthDtos {

    private AuthDtos() {}

    // ── Response: anonymous token ──────────────────────────────────────────────
    public record TokenResponse(
            String token,       // signed JWT
            String sessionId,   // the UUID embedded in the token (handy for client)
            long expiresIn      // seconds until expiry
    ) {}

    // ── Error shape (consistent with rest of system) ───────────────────────────
    public record ErrorResponse(
            int status,
            String error,
            String message
    ) {}
}
