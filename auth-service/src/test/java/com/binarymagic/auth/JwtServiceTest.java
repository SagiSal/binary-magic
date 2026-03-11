package com.binarymagic.auth;

import com.binarymagic.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // Use a 64-char secret so JJWT is happy with HS256
    private static final String SECRET =
            "test-secret-that-is-long-enough-for-hs256-needs-64-chars-padding!!";

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService(SECRET, 24);
    }

    @Test
    void generateAndValidate_roundTrip() {
        String sessionId = UUID.randomUUID().toString();
        String token = jwtService.generateAnonymousToken(sessionId);

        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractSessionId(token)).isEqualTo(sessionId);
    }

    @Test
    void differentCalls_produceDifferentTokens() {
        String t1 = jwtService.generateAnonymousToken(UUID.randomUUID().toString());
        String t2 = jwtService.generateAnonymousToken(UUID.randomUUID().toString());
        assertThat(t1).isNotEqualTo(t2);
    }

    @Test
    void tamperedToken_isInvalid() {
        String token = jwtService.generateAnonymousToken(UUID.randomUUID().toString());
        String tampered = token.substring(0, token.length() - 4) + "xxxx";
        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }

    @Test
    void getTtlSeconds_matches24Hours() {
        assertThat(jwtService.getTtlSeconds()).isEqualTo(86400L);
    }

    @Test
    void isTokenValid_withNullToken_returnsFalse() {
        assertThat(jwtService.isTokenValid(null)).isFalse();
    }

    @Test
    void isTokenValid_withEmptyToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("")).isFalse();
    }

    @Test
    void isTokenValid_withInvalidToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("invalid.token.here")).isFalse();
    }

    @Test
    void extractSessionId_withInvalidToken_throwsException() {
        assertThatThrownBy(() -> jwtService.extractSessionId("invalid.token"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void generateAnonymousToken_producesValidJwtStructure() {
        String sessionId = UUID.randomUUID().toString();
        String token = jwtService.generateAnonymousToken(sessionId);

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // JWT has header, payload, signature
    }
}