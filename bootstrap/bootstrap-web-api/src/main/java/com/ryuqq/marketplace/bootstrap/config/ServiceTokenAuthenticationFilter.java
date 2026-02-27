package com.ryuqq.marketplace.bootstrap.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 내부 서비스 간 통신용 X-Service-Token 인증 필터.
 *
 * <p>{@code /api/v1/market/internal/**} 경로에 대해 X-Service-Token 헤더를 검증합니다. 토큰이 유효하면 {@code
 * ROLE_INTERNAL_SERVICE} 권한으로 SecurityContext를 설정합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public class ServiceTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_SERVICE_TOKEN = "X-Service-Token";
    private static final String INTERNAL_PATH_PREFIX = "/api/v1/market/internal/";
    private static final String ROLE_INTERNAL_SERVICE = "ROLE_INTERNAL_SERVICE";
    private static final String INTERNAL_SERVICE_PRINCIPAL = "INTERNAL_SERVICE";

    private final String expectedToken;

    public ServiceTokenAuthenticationFilter(String expectedToken) {
        if (expectedToken == null || expectedToken.isBlank()) {
            throw new IllegalArgumentException("Service token must not be null or blank");
        }
        this.expectedToken = expectedToken;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(INTERNAL_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(HEADER_SERVICE_TOKEN);

        if (token != null
                && MessageDigest.isEqual(
                        token.getBytes(StandardCharsets.UTF_8),
                        expectedToken.getBytes(StandardCharsets.UTF_8))) {
            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(
                            INTERNAL_SERVICE_PRINCIPAL,
                            null,
                            List.of(new SimpleGrantedAuthority(ROLE_INTERNAL_SERVICE)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
