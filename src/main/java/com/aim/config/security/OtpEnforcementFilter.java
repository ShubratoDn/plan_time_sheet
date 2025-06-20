package com.aim.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class OtpEnforcementFilter extends OncePerRequestFilter {
    private static final List<String> ALLOWED_PATHS = Arrays.asList(
            "/login", "/logout", "/otp/entry", "/otp/validate", "/otp/resend", "/assets/", "/assets_new/", "/css/", "/js/", "/images/", "/static/", "/resources/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession(false);
        String path = request.getRequestURI();

        boolean isAllowed = ALLOWED_PATHS.stream().anyMatch(path::startsWith);
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));
        boolean otpVerified = session != null && Boolean.TRUE.equals(session.getAttribute("otp_verified"));

        if (isAuthenticated && !otpVerified && !isAllowed) {
            response.sendRedirect("/otp/entry");
            return;
        }
        filterChain.doFilter(request, response);
    }
} 