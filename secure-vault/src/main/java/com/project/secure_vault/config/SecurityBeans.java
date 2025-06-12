package com.project.secure_vault.config;

import com.project.secure_vault.security.JwtAuthenticationFilter;
import com.project.secure_vault.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBeans {

    private final JwtTokenProvider jwtProvider;

    public SecurityBeans(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider);
    }
}
