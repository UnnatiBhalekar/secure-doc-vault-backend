package com.project.secure_vault.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Extracts JWT from the Authorization header, validates it, 
 * and if valid, sets an Authentication in the SecurityContext.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
        throws ServletException, IOException {

        // Read the Authorization header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            // Validate the token
            if (jwtProvider.validateToken(token)) {
                String username = jwtProvider.getUsername(token);
                // Create an Authentication object (simple principal only)
                Authentication auth = new JwtAuthenticationToken(username);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        // Continue the filter chain
        chain.doFilter(request, response);
    }
}
