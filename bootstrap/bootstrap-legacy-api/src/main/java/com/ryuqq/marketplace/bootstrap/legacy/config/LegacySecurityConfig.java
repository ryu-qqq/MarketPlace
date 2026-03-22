package com.ryuqq.marketplace.bootstrap.legacy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyJwtAuthenticationFilter;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenCacheReadManager;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenManager;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Legacy API 전용 Spring Security 설정.
 *
 * <p>레거시 HS256 JWT 인증만 처리합니다. Gateway 인증 필터는 포함하지 않습니다. JWT 서명 검증 통과 시 claims를 신뢰하여 DB 조회 없이 인증을
 * 처리합니다.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class LegacySecurityConfig {

    @Bean
    public LegacyJwtAuthenticationFilter legacyJwtAuthenticationFilter(
            LegacyTokenManager legacyTokenManager,
            LegacyTokenCacheReadManager legacyTokenCacheReadManager) {
        return new LegacyJwtAuthenticationFilter(legacyTokenManager, legacyTokenCacheReadManager);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LegacyJwtAuthenticationFilter legacyJwtAuthenticationFilter,
            ObjectMapper objectMapper)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(
                                                (request, response, authException) ->
                                                        writeSecurityProblemDetail(
                                                                response,
                                                                objectMapper,
                                                                HttpStatus.UNAUTHORIZED,
                                                                "Unauthorized",
                                                                "인증이 필요합니다",
                                                                "AUTHENTICATION_REQUIRED",
                                                                request.getRequestURI()))
                                        .accessDeniedHandler(
                                                (request, response, accessDeniedException) ->
                                                        writeSecurityProblemDetail(
                                                                response,
                                                                objectMapper,
                                                                HttpStatus.FORBIDDEN,
                                                                "Forbidden",
                                                                "접근 권한이 없습니다",
                                                                "ACCESS_DENIED",
                                                                request.getRequestURI())))
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // 레거시 인증 (토큰 발급은 인증 없이 허용)
                                        .requestMatchers("/api/v1/legacy/auth/**")
                                        .permitAll()

                                        // Actuator
                                        .requestMatchers("/actuator/**")
                                        .permitAll()

                                        // 나머지는 인증 필요
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(
                        legacyJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeSecurityProblemDetail(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            HttpStatus status,
            String title,
            String detail,
            String errorCode,
            String requestUri)
            throws IOException {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("code", errorCode);
        pd.setInstance(URI.create(requestUri));

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader("x-error-code", errorCode);
        objectMapper.writeValue(response.getOutputStream(), pd);
    }
}
