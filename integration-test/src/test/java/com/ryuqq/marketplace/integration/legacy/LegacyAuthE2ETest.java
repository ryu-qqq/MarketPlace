package com.ryuqq.marketplace.integration.legacy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * 레거시 인증 API E2E 테스트.
 *
 * <p>테스트 대상: POST /api/v1/legacy/auth/authentication - 레거시 토큰 발급
 *
 * <p>인증 없이 접근 가능한 퍼블릭 엔드포인트입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("legacy")
@Tag("auth")
@DisplayName("레거시 인증 API E2E 테스트")
class LegacyAuthE2ETest extends LegacyE2ETestBase {

    private static final String AUTH_TOKEN_URL = "/api/v1/legacy/auth/authentication";

    @Nested
    @DisplayName("POST /api/v1/legacy/auth/authentication - 레거시 토큰 발급")
    class GetAccessTokenTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-LA-S01] 유효한 자격증명으로 토큰 발급 성공")
        void getAccessToken_ValidCredentials_Returns200() {
            // given - StubLegacySellerAuthCompositeQueryPort: stub@example.com → sellerId=10
            // StubLegacyPasswordEncoder: rawPassword.equals(encodedPassword) → 동일 비밀번호 허용
            Map<String, Object> request = validAuthRequest();

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(AUTH_TOKEN_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.token", notNullValue())
                    .body("response.status", equalTo(200))
                    .body("response.message", equalTo("success"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LA-F01] userId 누락 시 400 반환")
        void getAccessToken_MissingUserId_Returns400() {
            // given
            Map<String, Object> request = Map.of("password", "stub-hash", "roleType", "MASTER");

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(AUTH_TOKEN_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LA-F02] password 누락 시 400 반환")
        void getAccessToken_MissingPassword_Returns400() {
            // given
            Map<String, Object> request =
                    Map.of("userId", "stub@example.com", "roleType", "MASTER");

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(AUTH_TOKEN_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LA-F03] roleType 누락 시 400 반환")
        void getAccessToken_MissingRoleType_Returns400() {
            // given
            Map<String, Object> request =
                    Map.of("userId", "stub@example.com", "password", "stub-hash");

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(AUTH_TOKEN_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LA-F04] 빈 문자열 userId 전달 시 400 반환")
        void getAccessToken_BlankUserId_Returns400() {
            // given
            Map<String, Object> request =
                    Map.of("userId", "", "password", "stub-hash", "roleType", "MASTER");

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(AUTH_TOKEN_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== Helper 메서드 =====

    /**
     * 유효한 토큰 발급 요청 생성.
     *
     * <p>StubLegacySellerAuthCompositeQueryPort가 stub@example.com → sellerId=10을 반환하고,
     * StubLegacyPasswordEncoder가 stub-hash == stub-hash 로 비밀번호 검증을 통과합니다.
     */
    private Map<String, Object> validAuthRequest() {
        return Map.of("userId", "stub@example.com", "password", "stub-hash", "roleType", "MASTER");
    }
}
