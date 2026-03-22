package com.ryuqq.marketplace.adapter.in.rest.legacy.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenCacheReadManager;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenManager;
import jakarta.servlet.FilterChain;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyJwtAuthenticationFilter 테스트")
class LegacyJwtAuthenticationFilterTest {

    @Mock private LegacyTokenManager tokenManager;
    @Mock private LegacyTokenCacheReadManager tokenCacheReadManager;
    @Mock private FilterChain filterChain;

    private LegacyJwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String EXPIRED_TOKEN = "expired.jwt.token";
    private static final String REFRESH_TOKEN = "refresh.jwt.token";
    private static final String EMAIL = "seller@test.com";
    private static final long SELLER_ID = 1L;
    private static final String ROLE_TYPE = "SELLER";

    @BeforeEach
    void setUp() {
        filter = new LegacyJwtAuthenticationFilter(tokenManager, tokenCacheReadManager);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        LegacyAuthContextHolder.clear();
    }

    @Nested
    @DisplayName("shouldNotFilter")
    class ShouldNotFilterTest {

        @Test
        @DisplayName("레거시 경로가 아닌 요청은 필터링 안 함")
        void nonLegacyPath_ShouldNotFilter() {
            request.setRequestURI("/api/v1/market/products");
            assertThat(filter.shouldNotFilter(request)).isTrue();
        }

        @Test
        @DisplayName("레거시 인증 경로는 필터링 안 함")
        void authPath_ShouldNotFilter() {
            request.setRequestURI("/api/v1/legacy/auth/authentication");
            assertThat(filter.shouldNotFilter(request)).isTrue();
        }

        @Test
        @DisplayName("레거시 API 경로는 필터링 함")
        void legacyApiPath_ShouldFilter() {
            request.setRequestURI("/api/v1/legacy/product/group");
            assertThat(filter.shouldNotFilter(request)).isFalse();
        }
    }

    @Nested
    @DisplayName("doFilterInternal - 유효한 토큰")
    class ValidTokenTest {

        @Test
        @DisplayName("Bearer 토큰이 유효하면 SecurityContext와 LegacyAuthContext에 인증 정보 세팅")
        void validBearerToken_SetsAuthentication() throws Exception {
            request.setRequestURI("/api/v1/legacy/product/group");
            request.addHeader("Authorization", "Bearer " + VALID_TOKEN);

            given(tokenManager.isValid(VALID_TOKEN)).willReturn(true);
            given(tokenManager.extractSubject(VALID_TOKEN)).willReturn(EMAIL);
            given(tokenManager.extractSellerId(VALID_TOKEN)).willReturn(SELLER_ID);
            given(tokenManager.extractRole(VALID_TOKEN)).willReturn(ROLE_TYPE);

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                    .isEqualTo(EMAIL);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("API-KEY 헤더로도 인증 가능")
        void apiKeyHeader_SetsAuthentication() throws Exception {
            request.setRequestURI("/api/v1/legacy/product/group");
            request.addHeader("API-KEY", VALID_TOKEN);

            given(tokenManager.isValid(VALID_TOKEN)).willReturn(true);
            given(tokenManager.extractSubject(VALID_TOKEN)).willReturn(EMAIL);
            given(tokenManager.extractSellerId(VALID_TOKEN)).willReturn(SELLER_ID);
            given(tokenManager.extractRole(VALID_TOKEN)).willReturn(ROLE_TYPE);

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        }
    }

    @Nested
    @DisplayName("doFilterInternal - 만료된 토큰 + 리프레시")
    class ExpiredTokenWithRefreshTest {

        @Test
        @DisplayName("만료된 토큰이지만 리프레시 토큰이 유효하면 인증 통과")
        void expiredToken_ValidRefresh_SetsAuthentication() throws Exception {
            request.setRequestURI("/api/v1/legacy/product/group");
            request.addHeader("Authorization", "Bearer " + EXPIRED_TOKEN);

            given(tokenManager.isValid(EXPIRED_TOKEN)).willReturn(false);
            given(tokenManager.isExpired(EXPIRED_TOKEN)).willReturn(true);
            given(tokenManager.extractSubject(EXPIRED_TOKEN)).willReturn(EMAIL);
            given(tokenManager.extractSellerId(EXPIRED_TOKEN)).willReturn(SELLER_ID);
            given(tokenManager.extractRole(EXPIRED_TOKEN)).willReturn(ROLE_TYPE);
            given(tokenCacheReadManager.findByEmail(EMAIL)).willReturn(Optional.of(REFRESH_TOKEN));
            given(tokenManager.isValid(REFRESH_TOKEN)).willReturn(true);

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        }

        @Test
        @DisplayName("만료된 토큰이고 리프레시 토큰도 없으면 인증 안 됨")
        void expiredToken_NoRefresh_NoAuthentication() throws Exception {
            request.setRequestURI("/api/v1/legacy/product/group");
            request.addHeader("Authorization", "Bearer " + EXPIRED_TOKEN);

            given(tokenManager.isValid(EXPIRED_TOKEN)).willReturn(false);
            given(tokenManager.isExpired(EXPIRED_TOKEN)).willReturn(true);
            given(tokenManager.extractSubject(EXPIRED_TOKEN)).willReturn(EMAIL);
            given(tokenCacheReadManager.findByEmail(EMAIL)).willReturn(Optional.empty());

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("만료된 토큰이고 리프레시 토큰도 만료됐으면 인증 안 됨")
        void expiredToken_ExpiredRefresh_NoAuthentication() throws Exception {
            request.setRequestURI("/api/v1/legacy/product/group");
            request.addHeader("Authorization", "Bearer " + EXPIRED_TOKEN);

            given(tokenManager.isValid(EXPIRED_TOKEN)).willReturn(false);
            given(tokenManager.isExpired(EXPIRED_TOKEN)).willReturn(true);
            given(tokenManager.extractSubject(EXPIRED_TOKEN)).willReturn(EMAIL);
            given(tokenCacheReadManager.findByEmail(EMAIL)).willReturn(Optional.of(REFRESH_TOKEN));
            given(tokenManager.isValid(REFRESH_TOKEN)).willReturn(false);

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }

    @Nested
    @DisplayName("doFilterInternal - 토큰 없음")
    class NoTokenTest {

        @Test
        @DisplayName("토큰이 없으면 인증 없이 통과")
        void noToken_NoAuthentication() throws Exception {
            request.setRequestURI("/api/v1/legacy/product/group");

            filter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(tokenManager, never()).isValid(org.mockito.ArgumentMatchers.any());
        }
    }
}
