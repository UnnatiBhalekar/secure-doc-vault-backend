package com.project.secure_vault.config;

import com.project.secure_vault.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // @Bean
    // public CorsFilter corsFilter() {
    //     var config = new CorsConfiguration();
    //     config.setAllowedOrigins(List.of("http://localhost:4200"));
    //     config.setAllowedMethods(List.of("*"));
    //     config.setAllowedHeaders(List.of("*"));
    //     config.setAllowCredentials(true);

    //     var source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return new CorsFilter(source);
    // }

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200"));
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}


 @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  http
    .cors().and()
    .csrf(csrf -> csrf.disable())
    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
  .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
  .requestMatchers("/api/auth/**").permitAll()
  .requestMatchers(HttpMethod.GET,  "/api/documents").permitAll()
  .requestMatchers(HttpMethod.GET,  "/api/documents/*").permitAll()       // ← allow detail
  .requestMatchers(HttpMethod.POST, "/api/documents/upload").permitAll()
  .requestMatchers(HttpMethod.POST, "/api/documents/*/process").permitAll()
  .requestMatchers(HttpMethod.GET,  "/api/documents/*/download-redacted").permitAll()
  .anyRequest().denyAll()
)

    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

  return http.build();
}




}
