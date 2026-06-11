package com.destination.backend.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends GenericFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // 🔥 FIX: Allow product images without JWT
        if (path.startsWith("/product-image/")) {
            chain.doFilter(request, response);
            return;
        }

        if(path.startsWith("/payment-screenshots/")) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Public APIs
        if (path.startsWith("/api/admin/login") ||
                path.startsWith("/api/admin/create") ||
                path.startsWith("/api/admin/forgot-password") ||
                path.startsWith("/api/admin/update-password") ||
                path.startsWith("/api/admin/delete") ||
                path.startsWith("/api/admin/getAll") ||
                path.startsWith("/api/orders") ||
                path.startsWith("/api/products/getAll")) {

            chain.doFilter(request, response);
            return;
        }

        String header = req.getHeader("Authorization");

        // ❌ No token
        if (header == null || !header.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String token = header.substring(7);

        // ❌ Invalid token
        if (!jwtUtil.validateToken(token)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // 🔥 THIS IS THE MISSING PART
        String email = jwtUtil.extractEmail(token);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
                Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
