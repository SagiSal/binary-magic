package com.binarymagic.api_gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ErrorResponseFactory {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void writeError(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            objectMapper.writeValueAsString(Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase().toUpperCase().replace(" ", "_"),
                "message", message
            ))
        );
    }
}