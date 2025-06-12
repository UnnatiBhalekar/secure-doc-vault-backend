package com.project.secure_vault.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Collections;

/**
 * A basic Authentication implementation that holds only a principal (username).
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String principal;

    public JwtAuthenticationToken(String principal) {
        super(Collections.emptyList());
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
