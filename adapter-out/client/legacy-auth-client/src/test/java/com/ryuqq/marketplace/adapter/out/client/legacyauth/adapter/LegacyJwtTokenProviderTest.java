package com.ryuqq.marketplace.adapter.out.client.legacyauth.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.legacyauth.config.LegacyJwtProperties;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyJwtTokenProvider 통합 테스트.
 *
 * <p>실제 JWT 발급 → 파싱 라운드트립을 검증하여 레거시 어드민(setofAdmin) 토큰 형식과 호환되는지 확인합니다.
 *
 * <p>레거시 토큰 claims 구조: iss=setofAdmin, id=sellerId, sub=email, aud=roleType
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
        @DisplayName("발급된 토큰의 claims가 레거시 형식(iss, id, sub, aud)과 일치한다")
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

            assertThat(claims.getIssuer()).isEqualTo("setofAdmin");
            assertThat(claims.get("id", Number.class).longValue()).isEqualTo(SELLER_ID);
            assertThat(claims.getSubject()).isEqualTo(EMAIL);
            assertThat(claims.getAudience()).isEqualTo(ROLE_TYPE);
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.getExpiration()).isNotNull();
        }

        @Test
        @DisplayName("refreshToken도 동일한 claims 구조를 가진다")
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

            assertThat(claims.getIssuer()).isEqualTo("setofAdmin");
            assertThat(claims.get("id", Number.class).longValue()).isEqualTo(SELLER_ID);
            assertThat(claims.getAudience()).isEqualTo(ROLE_TYPE);
        }

        @Test
        @DisplayName("MASTER 역할도 aud claim에 정확히 들어간다")
        void generateToken_MasterRole_SetsAudCorrectly() {
            // when
            LegacyTokenResult result =
                    sut.generateToken("admin@trexi.co.kr", 1L, "MASTER");

            // then
            Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(verificationKey)
                            .build()
                            .parseClaimsJws(result.accessToken())
                            .getBody();

            assertThat(claims.getAudience()).isEqualTo("MASTER");
            assertThat(claims.get("id", Number.class).longValue()).isEqualTo(1L);
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
            assertThat(sut.extractSellerId(token)).isEqualTo(SELLER_ID);
            assertThat(sut.extractRole(token)).isEqualTo(ROLE_TYPE);
        }
    }

    @Nested
    @DisplayName("레거시 어드민 발급 토큰 파싱 호환")
    class LegacyAdminTokenParsingTest {

        @Test
        @DisplayName("레거시 어드민 형식 토큰(iss=setofAdmin, id, aud)을 정확히 파싱한다")
        void parseLegacyAdminToken_ExtractsCorrectClaims() {
            // given — 레거시 어드민이 발급하는 형식의 토큰을 직접 생성
            String legacyAdminToken =
                    Jwts.builder()
                            .setIssuer("setofAdmin")
                            .claim("id", 78)
                            .setSubject("jace@claps.kr")
                            .setAudience("SELLER")
                            .setIssuedAt(new java.util.Date())
                            .setExpiration(
                                    new java.util.Date(
                                            System.currentTimeMillis() + 10800000L))
                            .signWith(
                                    verificationKey,
                                    io.jsonwebtoken.SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(legacyAdminToken)).isTrue();
            assertThat(sut.extractSubject(legacyAdminToken)).isEqualTo("jace@claps.kr");
            assertThat(sut.extractSellerId(legacyAdminToken)).isEqualTo(78L);
            assertThat(sut.extractRole(legacyAdminToken)).isEqualTo("SELLER");
        }

        @Test
        @DisplayName("MASTER 역할 레거시 어드민 토큰도 정확히 파싱한다")
        void parseLegacyAdminToken_MasterRole_ExtractsCorrectly() {
            // given
            String masterToken =
                    Jwts.builder()
                            .setIssuer("setofAdmin")
                            .claim("id", 1)
                            .setSubject("woorang@trexi.co.kr")
                            .setAudience("MASTER")
                            .setIssuedAt(new java.util.Date())
                            .setExpiration(
                                    new java.util.Date(
                                            System.currentTimeMillis() + 10800000L))
                            .signWith(
                                    verificationKey,
                                    io.jsonwebtoken.SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(masterToken)).isTrue();
            assertThat(sut.extractSubject(masterToken)).isEqualTo("woorang@trexi.co.kr");
            assertThat(sut.extractSellerId(masterToken)).isEqualTo(1L);
            assertThat(sut.extractRole(masterToken)).isEqualTo("MASTER");
        }
    }

    @Nested
    @DisplayName("유효성 검증")
    class ValidationTest {

        @Test
        @DisplayName("다른 secret으로 서명된 토큰은 invalid 처리된다")
        void differentSecret_ReturnsInvalid() {
            // given
            Key otherKey = Keys.hmacShaKeyFor(
                    "other-secret-key-must-be-at-least-256-bits-long-for-hs256".getBytes());
            String otherToken =
                    Jwts.builder()
                            .setSubject("hacker@evil.com")
                            .signWith(otherKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(otherToken)).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰은 isExpired=true, isValid=false")
        void expiredToken_IsExpiredTrue() {
            // given — 이미 만료된 토큰 생성
            String expiredToken =
                    Jwts.builder()
                            .setIssuer("setofAdmin")
                            .claim("id", 78)
                            .setSubject("jace@claps.kr")
                            .setAudience("SELLER")
                            .setIssuedAt(
                                    new java.util.Date(
                                            System.currentTimeMillis() - 7200000L))
                            .setExpiration(
                                    new java.util.Date(
                                            System.currentTimeMillis() - 3600000L))
                            .signWith(
                                    verificationKey,
                                    io.jsonwebtoken.SignatureAlgorithm.HS256)
                            .compact();

            // when & then
            assertThat(sut.isValid(expiredToken)).isFalse();
            assertThat(sut.isExpired(expiredToken)).isTrue();
            // 만료되어도 claims 추출은 가능해야 함
            assertThat(sut.extractSubject(expiredToken)).isEqualTo("jace@claps.kr");
            assertThat(sut.extractSellerId(expiredToken)).isEqualTo(78L);
            assertThat(sut.extractRole(expiredToken)).isEqualTo("SELLER");
        }
    }
}
