package com.ryuqq.marketplace.integration.legacy;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.client.legacyauth.adapter.LegacyJwtTokenProvider;
import com.ryuqq.marketplace.adapter.out.client.legacyauth.config.LegacyJwtProperties;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * JWT 토큰 발급 → claims 검증 → API 호출 E2E 테스트.
 *
 * <p>auth 엔드포인트에서 발급된 실제 JWT의 claims 구조가 레거시 어드민 형식과 동일한지 검증하고,
 * 해당 토큰으로 보호된 API 호출이 성공하는지 확인합니다.
 *
 * <p>인증 필터는 Stub LegacyTokenClient를 사용하므로 JWT 서명 검증은 수행하지 않습니다.
 * 대신 실제 {@link LegacyJwtTokenProvider}를 직접 생성하여 발급된 JWT의 claims를 파싱/검증합니다.
 *
 * <p>JWT 서명 검증/거부 로직은 {@code LegacyJwtTokenProviderTest}와 {@code LegacyJwtAuthenticationFilterTest}에서
 * 단위 테스트로 커버합니다.
 */
@Tag("e2e")
@Tag("legacy")
@Tag("auth")
@DisplayName("JWT 토큰 라운드트립 E2E 테스트 — 토큰 발급 + claims 검증 + API 호출")
class LegacyJwtRoundTripE2ETest extends LegacyE2ETestBase {

    private static final String AUTH_TOKEN_URL = "/api/v1/legacy/auth/authentication";
    private static final String SELLER_URL = "/api/v1/legacy/seller";
    private static final String SECRET =
            "test-legacy-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256";

    /**
     * application-test.yml의 legacy.token.secret과 동일한 secret으로 실제 LegacyJwtTokenProvider를 생성하여
     * 발급된 JWT의 claims를 직접 검증합니다.
     */
    private LegacyJwtTokenProvider realTokenProvider;

    @BeforeEach
    void setUpRealProvider() {
        LegacyJwtProperties props = new LegacyJwtProperties();
        props.setSecret(SECRET);
        props.setAccessTokenExpireTime(1_800_000L);
        props.setRefreshTokenExpireTime(10_800_000L);
        realTokenProvider = new LegacyJwtTokenProvider(props);
        realTokenProvider.init();
    }

    @Nested
    @DisplayName("토큰 발급 + claims 구조 검증")
    class TokenFormatTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-JWT-FMT01] 발급된 토큰이 레거시 형식(sub, role)이고 iss/id/aud가 없다")
        void issuedToken_HasLegacyClaimsFormat() {
            // given — 실제 JWT 발급
            String token = issueRealToken("stub@example.com", "stub-hash", "MASTER");

            // then — 실제 provider로 claims 파싱
            assertThat(realTokenProvider.isValid(token)).isTrue();
            assertThat(realTokenProvider.extractSubject(token)).isEqualTo("stub@example.com");
            assertThat(realTokenProvider.extractRole(token)).isEqualTo("MASTER");
            // 새 레거시 형식에는 id claim이 없으므로 sellerId=0
            assertThat(realTokenProvider.extractSellerId(token)).isEqualTo(0L);
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-JWT-FMT02] SELLER 역할로 발급된 토큰의 role claim이 SELLER이다")
        void issuedSellerToken_RoleClaimIsSeller() {
            // given
            String token = issueRealToken("stub@example.com", "stub-hash", "SELLER");

            // then
            assertThat(realTokenProvider.extractRole(token)).isEqualTo("SELLER");
            assertThat(realTokenProvider.extractSubject(token)).isEqualTo("stub@example.com");
            assertThat(realTokenProvider.extractSellerId(token)).isEqualTo(0L);
        }

        @Test
        @DisplayName("[TC-JWT-FMT03] 발급된 토큰은 만료되지 않은 상태이다")
        void issuedToken_IsNotExpired() {
            // given
            String token = issueRealToken("stub@example.com", "stub-hash", "MASTER");

            // then
            assertThat(realTokenProvider.isValid(token)).isTrue();
            assertThat(realTokenProvider.isExpired(token)).isFalse();
        }
    }

    @Nested
    @DisplayName("토큰 발급 → API 호출 라운드트립")
    class TokenRoundTripTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-JWT-RT01] 발급된 JWT로 보호된 API 호출 시 인증 통과 (200)")
        void issueToken_ThenCallProtectedApi_Returns200() {
            // given — 실제 JWT 발급
            String token = issueRealToken("stub@example.com", "stub-hash", "MASTER");

            // when & then — 발급된 JWT로 보호된 API 호출
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get(SELLER_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.sellerId", equalTo(10))
                    .body("data.sellerName", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-JWT-RT02] 발급된 JWT를 API-KEY 헤더로 전달해도 인증 통과")
        void issueToken_UseApiKeyHeader_Returns200() {
            // given
            String token = issueRealToken("stub@example.com", "stub-hash", "MASTER");

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("API-KEY", token)
                    .when()
                    .get(SELLER_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-JWT-RT03] 토큰 없이 보호된 API 호출 시 인증 실패 (401)")
        void noToken_CallProtectedApi_Returns401() {
            // when & then
            givenUnauthenticated()
                    .when()
                    .get(SELLER_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== Helper 메서드 =====

    /**
     * POST /auth/authentication으로 실제 JWT를 발급받습니다.
     *
     * <p>StubSellerAuthQueryPort가 email → sellerId=10, password=stub-hash를 반환합니다.
     * 실제 LegacyJwtTokenProvider가 JWT를 생성합니다 (application-test.yml의 secret 사용).
     */
    private String issueRealToken(String email, String password, String roleType) {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("userId", email, "password", password, "roleType", roleType))
                .when()
                .post(AUTH_TOKEN_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.token", notNullValue())
                .extract()
                .path("data.token");
    }
}
