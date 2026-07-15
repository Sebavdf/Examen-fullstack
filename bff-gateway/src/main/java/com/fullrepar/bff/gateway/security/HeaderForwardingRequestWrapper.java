package com.fullrepar.bff.gateway.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds the username/role extracted from the validated JWT as extra request
 * headers (X-User-Name, X-User-Role) so downstream microservices can read
 * the caller's identity without needing to parse the token themselves.
 */
public class HeaderForwardingRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> extraHeaders = new HashMap<>();

    public HeaderForwardingRequestWrapper(HttpServletRequest request, String username, String role) {
        super(request);
        if (username != null) {
            extraHeaders.put("X-User-Name", username);
        }
        if (role != null) {
            extraHeaders.put("X-User-Role", role);
        }
    }

    @Override
    public String getHeader(String name) {
        String value = extraHeaders.get(name);
        return value != null ? value : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String value = extraHeaders.get(name);
        return value != null ? Collections.enumeration(Collections.singletonList(value))
                              : super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        java.util.List<String> names = Collections.list(super.getHeaderNames());
        for (String name : extraHeaders.keySet()) {
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        return Collections.enumeration(names);
    }
}
