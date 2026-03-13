package com.binarymagic.api_gateway.filter;

import com.binarymagic.api_gateway.security.JwtValidator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Auth endpoints and actuator are public — skip JWT check
        if (path.startsWith("/api/auth/") || path.startsWith("/actuator/")) {
            chain.doFilter(request, response);
            return;
        }

        // Extract Bearer token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Missing or malformed Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        // Validate signature and expiry
        if (!jwtValidator.isValid(token)) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        // Inject session ID as header so downstream services don't need to re-parse the JWT
        String sessionId = jwtValidator.extractSessionId(token);
        HttpServletRequest mutated = new SessionIdRequestWrapper(request, sessionId);

        chain.doFilter(mutated, response);
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
                status.getReasonPhrase().toUpperCase().replace(" ", "_"), message)
        );
    }
}