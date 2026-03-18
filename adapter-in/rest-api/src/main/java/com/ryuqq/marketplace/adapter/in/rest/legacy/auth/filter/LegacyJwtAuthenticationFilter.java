package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.filter;

import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacySellerAuthCompositeReadManager;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenCacheReadManager;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 레거시 JWT 인증 필터.
 *
 * <p>/api/v1/legacy/** 경로에만 적용. 레거시 HS256 JWT를 검증하고 SecurityContext에 인증 정보를 세팅합니다.
 *
 * <p>AccessToken 만료 시 Redis의 RefreshToken으로 자동 인증 (레거시 호환).
 */
public class LegacyJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String API_KEY_HEADER = "API-KEY";
    private static final String LEGACY_PATH_PREFIX = "/api/v1/legacy/";
    private static final String AUTH_PATH = "/api/v1/legacy/auth/";

    private final LegacyTokenManager tokenManager;
    private final LegacySellerAuthCompositeReadManager sellerAuthReadManager;
    private final LegacyTokenCacheReadManager tokenCacheReadManager;

    public LegacyJwtAuthenticationFilter(
            LegacyTokenManager tokenManager,
            LegacySellerAuthCompositeReadManager sellerAuthReadManager,
            LegacyTokenCacheReadManager tokenCacheReadManager) {
        this.tokenManager = tokenManager;
        this.sellerAuthReadManager = sellerAuthReadManager;
        this.tokenCacheReadManager = tokenCacheReadManager;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith(LEGACY_PATH_PREFIX)) {
            return true;
        }
        return path.startsWith(AUTH_PATH);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> token = resolveToken(request);

        if (token.isPresent()) {
            String jwt = token.get();
            String email = null;

            if (tokenManager.isValid(jwt)) {
                email = tokenManager.extractSubject(jwt);
            } else if (tokenManager.isExpired(jwt)) {
                email = tokenManager.extractSubject(jwt);
                if (!tryRefreshAuthentication(email)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            if (email != null) {
                setAuthentication(email);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean tryRefreshAuthentication(String email) {
        Optional<String> refreshToken = tokenCacheReadManager.findByEmail(email);
        return refreshToken.isPresent() && tokenManager.isValid(refreshToken.get());
    }

    private void setAuthentication(String email) {
        try {
            LegacySellerAuthResult authResult = sellerAuthReadManager.getByEmail(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            authResult.email(),
                            null,
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(authResult.roleType())));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            // 셀러 조회 실패 시 인증 없이 진행 → Spring Security가 401 처리
        }
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.startsWith(BEARER_PREFIX)) {
                return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
            }
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (StringUtils.hasText(apiKey)) {
            return Optional.of(apiKey);
        }

        return Optional.empty();
    }
}
