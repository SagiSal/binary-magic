package com.binarymagic.api_gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

// HttpServletRequest is immutable — we wrap it to inject an extra header
public class SessionIdRequestWrapper extends HttpServletRequestWrapper {

    private final String sessionId;

    public SessionIdRequestWrapper(HttpServletRequest request, String sessionId) {
        super(request);
        this.sessionId = sessionId;
    }

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
}