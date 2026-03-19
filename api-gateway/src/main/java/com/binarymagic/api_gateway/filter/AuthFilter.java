package com.binarymagic.api_gateway.filter;

import com.binarymagic.api_gateway.exception.ErrorResponseFactory;
import com.binarymagic.api_gateway.security.JwtValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final ErrorResponseFactory errorResponseFactory;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Public routes — skip JWT check
        if (path.startsWith("/api/auth/") || path.startsWith("/actuator/")) {
            chain.doFilter(request, response);
            return;
        }

        // Extract Bearer token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            errorResponseFactory.writeError(response, HttpStatus.UNAUTHORIZED,
                "Missing or malformed Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        // Validate token
        if (!jwtValidator.isValid(token)) {
            errorResponseFactory.writeError(response, HttpStatus.UNAUTHORIZED,
                "Invalid or expired token");
            return;
        }

        // Extract sessionId and inject as X-Session-Id header downstream
        String sessionId = jwtValidator.extractSessionId(token);
        HttpServletRequest mutated = new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("X-Session-Id".equalsIgnoreCase(name)) return sessionId;
                return super.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if ("X-Session-Id".equalsIgnoreCase(name))
                    return Collections.enumeration(List.of(sessionId));
                return super.getHeaders(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                List<String> names = Collections.list(super.getHeaderNames());
                names.add("X-Session-Id");
                return Collections.enumeration(names);
            }
        };

        chain.doFilter(mutated, response);
    }
}