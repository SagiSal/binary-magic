package com.binarymagic.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey signingKey;
    private final long anonymousTtlHours;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.anonymous-ttl-hours}") long anonymousTtlHours
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.anonymousTtlHours = anonymousTtlHours;
    }

    /**
     * Mint an anonymous JWT.
     * sub  = random UUID (the session identity)
     * type = "anonymous" — future login tokens will use type="authenticated"
     *        so the gateway can tell them apart when needed.
     */
    public String generateAnonymousToken(String sessionId) {
        Instant now    = Instant.now();
        Instant expiry = now.plusSeconds(anonymousTtlHours * 3600);

        return Jwts.builder()
                .subject(sessionId)
                .claim("type", "anonymous")
                .id(UUID.randomUUID().toString())   // jti — unique per token
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public String extractSessionId(String token) {
        return parse(token).getSubject();
    }

    public long getTtlSeconds() {
        return anonymousTtlHours * 3600;
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
