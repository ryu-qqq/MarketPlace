package com.ryuqq.marketplace.adapter.in.rest.legacy.common.security;

import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacySellerAuthCompositeReadManager;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenCacheReadManager;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenManager;
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
 * <p>/api/v1/legacy/** 경로에만 적용. HS256 JWT 서명을 검증하고 claims에서 인증 정보를 추출하여 {@link
 * LegacyAuthContextHolder}와 {@link SecurityContextHolder}에 세팅합니다.
 *
 * <p>레거시 토큰에 sellerId(id claim)가 없는 경우 DB 조회로 해소합니다. AccessToken 만료 시 Redis의 RefreshToken으로 자동 인증
 * (레거시 호환).
 */
public class LegacyJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String API_KEY_HEADER = "API-KEY";
    private static final String LEGACY_PATH_PREFIX = "/api/v1/legacy/";
    private static final String AUTH_PATH = "/api/v1/legacy/auth/";

    private final LegacyTokenManager tokenManager;
    private final LegacyTokenCacheReadManager tokenCacheReadManager;
    private final LegacySellerAuthCompositeReadManager sellerAuthReadManager;

    public LegacyJwtAuthenticationFilter(
            LegacyTokenManager tokenManager,
            LegacyTokenCacheReadManager tokenCacheReadManager,
            LegacySellerAuthCompositeReadManager sellerAuthReadManager) {
        this.tokenManager = tokenManager;
        this.tokenCacheReadManager = tokenCacheReadManager;
        this.sellerAuthReadManager = sellerAuthReadManager;
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

        try {
            Optional<String> token = resolveToken(request);

            if (token.isPresent()) {
                String jwt = token.get();
                if (canAuthenticate(jwt)) {
                    setAuthentication(jwt);
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            LegacyAuthContextHolder.clear();
        }
    }

    private boolean canAuthenticate(String jwt) {
        if (tokenManager.isValid(jwt)) {
            return true;
        }
        if (tokenManager.isExpired(jwt)) {
            String email = tokenManager.extractSubject(jwt);
            return tryRefreshAuthentication(email);
        }
        return false;
    }

    private boolean tryRefreshAuthentication(String email) {
        Optional<String> refreshToken = tokenCacheReadManager.findByEmail(email);
        return refreshToken.isPresent() && tokenManager.isValid(refreshToken.get());
    }

    private void setAuthentication(String jwt) {
        String email = tokenManager.extractSubject(jwt);
        long sellerId = tokenManager.extractSellerId(jwt);
        String roleType = tokenManager.extractRole(jwt);

        if (sellerId == 0L) {
            LegacySellerAuthResult sellerAuth = sellerAuthReadManager.getByEmail(email);
            sellerId = sellerAuth.sellerId();
            if (roleType == null) {
                roleType = sellerAuth.roleType();
            }
        }

        LegacyAuthContextHolder.setContext(new LegacyAuthContext(sellerId, email, roleType));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(roleType)));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return Optional.of(bearerToken.substring(BEARER_PREFIX.length()));
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (StringUtils.hasText(apiKey)) {
            return Optional.of(apiKey);
        }

        return Optional.empty();
    }
}
