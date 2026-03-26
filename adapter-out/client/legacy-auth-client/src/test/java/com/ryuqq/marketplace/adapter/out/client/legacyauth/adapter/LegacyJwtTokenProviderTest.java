package com.ryuqq.marketplace.adapter.out.client.legacyauth.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.legacyauth.config.LegacyJwtProperties;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyJwtTokenProvider 단위 테스트.
 *
 * <p>실제 JWT 발급 → 파싱 라운드트립을 검증하여 레거시 어드민 토큰 형식과 호환되는지 확인합니다.
 *
 * <p>레거시 토큰 claims 구조: sub=email, role=roleType (iss, id, aud 없음)
 */
@Tag("unit")
@DisplayName("LegacyJwtTokenProvider — 레거시 토큰 형식 호환 테스트")
class LegacyJwtTokenProviderTest {

    private static final String SECRET =
            "test-legacy-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256";
    private static final String EMAIL = "jace@claps.kr";
    private static final long SELLER_ID = 78L;
    private static final String ROLE_TYPE = "SELLER";

    private LegacyJwtTokenProvider sut;
    private Key verificationKey;

    @BeforeEach
    void setUp() {
        LegacyJwtProperties properties = new LegacyJwtProperties();
        properties.setSecret(SECRET);
        properties.setAccessTokenExpireTime(1800000L);
        properties.setRefreshTokenExpireTime(10800000L);

        sut = new LegacyJwtTokenProvider(properties);
        sut.init();

        verificationKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    @Nested
    @DisplayName("generateToken — 토큰 발급")
    class GenerateTokenTest {

        @Test
        @DisplayName("발급된 토큰의 claims가 레거시 형식(sub, role)과 일치한다")
        void generateToken_ProducesLegacyClaimsStructure() {
            // when
            LegacyTokenResult result = sut.generateToken(EMAIL, SELLER_ID, ROLE_TYPE);

            // then — 실제 JWT를 직접 디코딩하여 claims 구조 검증
            Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(verificationKey)
                            .build()
                            .parseClaimsJws(result.accessToken())
                            .getBody();

            assertThat(claims.getSubject()).isEqualTo(EMAIL);
            assertThat(claims.get("role", String.class)).isEqualTo(ROLE_TYPE);
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.getExpiration()).isNotNull();
            // 레거시 형식에는 iss, id, aud가 없어야 한다
            assertThat(claims.getIssuer()).isNull();
            assertThat(claims.get("id")).isNull();
            assertThat(claims.getAudience()).isNull();
        }

        @Test
        @DisplayName("refreshToken도 동일한 레거시 claims 구조를 가진다")
        void generateToken_RefreshTokenAlsoHasLegacyClaims() {
            // when
            LegacyTokenResult result = sut.generateToken(EMAIL, SELLER_ID, ROLE_TYPE);

            // then
            Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(verificationKey)
                            .build()
                            .parseClaimsJws(result.refreshToken())
                            .getBody();

            assertThat(claims.getSubject()).isEqualTo(EMAIL);
            assertThat(claims.get("role", String.class)).isEqualTo(ROLE_TYPE);
            assertThat(claims.getIssuer()).isNull();
            assertThat(claims.get("id")).isNull();
        }

        @Test
        @DisplayName("MASTER 역할도 role claim에 정확히 들어간다")
        void generateToken_MasterRole_SetsRoleCorrectly() {
            // when
            LegacyTokenResult result = sut.generateToken("admin@trexi.co.kr", 1L, "MASTER");

            // then
            Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(verificationKey)
                            .build()
                            .parseClaimsJws(result.accessToken())
                            .getBody();

            assertThat(claims.get("role", String.class)).isEqualTo("MASTER");
        }
    }

    @Nested
    @DisplayName("발급 → 파싱 라운드트립")
    class RoundTripTest {

        @Test
        @DisplayName("발급한 토큰을 다시 파싱하면 동일한 정보가 추출된다")
        void generateAndExtract_RoundTrip_Succeeds() {
            // given
            LegacyTokenResult result = sut.generateToken(EMAIL, SELLER_ID, ROLE_TYPE);
            String token = result.accessToken();

            // when & then
            assertThat(sut.isValid(token)).isTrue();
            assertThat(sut.isExpired(token)).isFalse();
            assertThat(sut.extractSubject(token)).isEqualTo(EMAIL);
            assertThat(sut.extractRole(token)).isEqualTo(ROLE_TYPE);
            // 레거시 형식에는 sellerId가 없으므로 0 반환
            assertThat(sut.extractSellerId(token)).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("레거시 어드민 발급 토큰 파싱 호환")
    class LegacyAdminTokenParsingTest {

        @Test
        @DisplayName("레거시 어드민 형식 토큰(sub, role)을 정확히 파싱한다")
        void parseLegacyAdminToken_ExtractsCorrectClaims() {
            // given
            String legacyAdminToken =
                    Jwts.builder()
                            .setSubject("jace@claps.kr")
                            .claim("role", "SELLER")
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 10800000L))
                            .signWith(verificationKey, SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(legacyAdminToken)).isTrue();
            assertThat(sut.extractSubject(legacyAdminToken)).isEqualTo("jace@claps.kr");
            assertThat(sut.extractRole(legacyAdminToken)).isEqualTo("SELLER");
            assertThat(sut.extractSellerId(legacyAdminToken)).isEqualTo(0L);
        }

        @Test
        @DisplayName("GUEST 역할 레거시 어드민 토큰도 정확히 파싱한다")
        void parseLegacyAdminToken_GuestRole_ExtractsCorrectly() {
            // given — Shadow Traffic에서 캡처된 실제 레거시 형식
            String guestToken =
                    Jwts.builder()
                            .setSubject("trexi789")
                            .claim("role", "GUEST")
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 10800000L))
                            .signWith(verificationKey, SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(guestToken)).isTrue();
            assertThat(sut.extractSubject(guestToken)).isEqualTo("trexi789");
            assertThat(sut.extractRole(guestToken)).isEqualTo("GUEST");
        }
    }

    @Nested
    @DisplayName("기존 MarketPlace 형식(iss, id, aud) 토큰 하위 호환")
    class MarketPlaceTokenBackwardCompatibilityTest {

        @Test
        @DisplayName("기존 MarketPlace 형식 토큰도 정확히 파싱한다")
        void parseMarketPlaceToken_ExtractsCorrectClaims() {
            // given — 과도기 동안 기존 형식 토큰이 남아있을 수 있음
            String marketPlaceToken =
                    Jwts.builder()
                            .setIssuer("setofAdmin")
                            .claim("id", 78)
                            .setSubject("jace@claps.kr")
                            .setAudience("SELLER")
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 10800000L))
                            .signWith(verificationKey, SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(marketPlaceToken)).isTrue();
            assertThat(sut.extractSubject(marketPlaceToken)).isEqualTo("jace@claps.kr");
            assertThat(sut.extractSellerId(marketPlaceToken)).isEqualTo(78L);
            // aud에서 역할 추출 (role claim 없으므로 fallback)
            assertThat(sut.extractRole(marketPlaceToken)).isEqualTo("SELLER");
        }
    }

    @Nested
    @DisplayName("유효성 검증")
    class ValidationTest {

        @Test
        @DisplayName("다른 secret으로 서명된 토큰은 invalid 처리된다")
        void differentSecret_ReturnsInvalid() {
            // given
            Key otherKey =
                    Keys.hmacShaKeyFor(
                            "other-secret-key-must-be-at-least-256-bits-long-for-hs256".getBytes());
            String otherToken =
                    Jwts.builder()
                            .setSubject("hacker@evil.com")
                            .signWith(otherKey, SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(otherToken)).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰은 isExpired=true, isValid=false")
        void expiredToken_IsExpiredTrue() {
            // given — 이미 만료된 레거시 형식 토큰 생성
            String expiredToken =
                    Jwts.builder()
                            .setSubject("jace@claps.kr")
                            .claim("role", "SELLER")
                            .setIssuedAt(new Date(System.currentTimeMillis() - 7200000L))
                            .setExpiration(new Date(System.currentTimeMillis() - 3600000L))
                            .signWith(verificationKey, SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(expiredToken)).isFalse();
            assertThat(sut.isExpired(expiredToken)).isTrue();
            assertThat(sut.extractSubject(expiredToken)).isEqualTo("jace@claps.kr");
            assertThat(sut.extractRole(expiredToken)).isEqualTo("SELLER");
        }
    }
}
