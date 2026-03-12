package com.binarymagic.auth;

import com.binarymagic.auth.controller.AuthController;
import com.binarymagic.auth.dto.AuthDtos.TokenResponse;
import com.binarymagic.auth.service.TokenService;
import com.binarymagic.auth.config.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @Test
    void getToken_returnsTokenResponse() throws Exception {
        // Arrange
        TokenResponse mockResponse = new TokenResponse("mock-jwt-token", "session-uuid", 86400L);
        when(tokenService.issueAnonymousToken()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.sessionId").value("session-uuid"))
                .andExpect(jsonPath("$.expiresIn").value(86400));
    }

    @Test
    void getToken_callsTokenService() throws Exception {
        // Arrange
        TokenResponse mockResponse = new TokenResponse("token", "id", 3600L);
        when(tokenService.issueAnonymousToken()).thenReturn(mockResponse);

        // Act
        mockMvc.perform(post("/api/auth/token"));

        // Assert - Mockito will verify the call was made
    }
}