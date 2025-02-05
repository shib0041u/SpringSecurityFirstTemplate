package com.org.B_SpringSecurity_role.services;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        // Extract all roles into a set for efficient checks
        Set<String> roles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());
//        System.out.println(roles);

        String targetUrl;

        // Determine the target URL based on roles
        if (roles.contains("ROLE_ADMIN")) {
//        	System.out.println("here");
            targetUrl = "/admin";
        } else if (roles.contains("ROLE_USER")) {
            targetUrl = "/user";
        } else if (roles.contains("ROLE_STUDENT")) {
            targetUrl = "/student";
        } else if (roles.contains("ROLE_VENDOR")) {
            targetUrl = "/vendor";
        } else {
            targetUrl = "/home"; // Default target for unknown roles
        }

        // Redirect to the determined target URL
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
