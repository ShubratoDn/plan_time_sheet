package com.aim.config.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {
        String errorMessage = "Invalid username or password";
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "Username not found";
        } else if (exception instanceof DisabledException) {
            errorMessage = "User is not active";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Password incorrect";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "User is not active";
        }
        request.getSession().setAttribute("error", errorMessage);
        response.sendRedirect("/login");
    }
} 