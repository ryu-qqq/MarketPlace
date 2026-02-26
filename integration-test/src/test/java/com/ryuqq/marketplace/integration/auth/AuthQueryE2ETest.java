package com.ryuqq.marketplace.integration.auth;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * Auth Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /admin/auth/me - 내 정보 조회
 *
 * <p>우선순위: - P0: 5개 시나리오 (필수 기능)
 *
 * <p>주의사항: - Authorization 헤더 처리 필요 - JWT 토큰 검증 로직 포함
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("auth")
@Tag("query")
@DisplayName("Auth Query API E2E 테스트")
class AuthQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/auth";

    @BeforeEach
    void setUp() {
        // AuthHub Mock 설정 (실제 구현 시 추가)
    }

    // ===== GET /admin/auth/me - 내 정보 조회 =====

    @Nested
    @DisplayName("GET /admin/auth/me - 내 정보 조회")
    class GetMyInfoTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-013] 유효한 토큰으로 내 정보 조회 성공")
        void getMyInfo_ValidToken_Returns200() {
            // given: 먼저 로그인하여 토큰 발급
            Map<String, Object> loginRequest =
                    Map.of(
                            "identifier", "admin@example.com",
                            "password", "password123!");

            Response loginResponse =
                    given().spec(givenSuperAdmin())
                            .body(loginRequest)
                            .when()
                            .post(BASE_URL + "/login");

            String accessToken = loginResponse.jsonPath().getString("data.accessToken");

            // when: 내 정보 조회 (인증된 사용자 컨텍스트)
            Response response =
                    given().spec(givenAuthenticatedUser())
                            .header("Authorization", "Bearer " + accessToken)
                            .when()
                            .get(BASE_URL + "/me");

            // then: @PreAuthorize("@access.authenticated()") 검증 통과
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.userId", notNullValue())
                    .body("data.email", equalTo("admin@example.com"))
                    .body("data.name", notNullValue())
                    .body("data.tenantId", notNullValue())
                    .body("data.tenantName", notNullValue())
                    .body("data.organizationId", notNullValue())
                    .body("data.organizationName", notNullValue())
                    .body("data.roles", notNullValue())
                    .body("data.permissions", notNullValue());

            // 추가 검증
            String userId = response.jsonPath().getString("data.userId");
            assertThat(userId).isNotBlank();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-014] Authorization 헤더 없이 요청 시 401")
        void getMyInfo_NoAuthorizationHeader_Returns401() {
            // given: 인증 헤더 없이 요청
            // when & then: @PreAuthorize("@access.authenticated()") 검증 실패
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-015] 잘못된 형식의 Authorization 헤더 시 401")
        void getMyInfo_InvalidAuthorizationFormat_Returns401() {
            // given: "Bearer" 접두사 없는 잘못된 형식
            String invalidToken = "InvalidFormat eyJhbGciOiJIUzI1NiJ9...";

            // when & then: @PreAuthorize("@access.authenticated()") 검증 실패
            given().spec(givenUnauthenticated())
                    .header("Authorization", invalidToken)
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-016] 만료된 토큰으로 요청 시 401")
        void getMyInfo_ExpiredToken_Returns401() {
            // given: 만료된 토큰 (AuthHub SDK Mock 필요)
            String expiredToken = "eyJhbGciOiJIUzI1NiJ9.expired.token";

            // when & then: @PreAuthorize("@access.authenticated()") 검증 실패
            given().spec(givenUnauthenticated())
                    .header("Authorization", "Bearer " + expiredToken)
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-017] 유효하지 않은 토큰 (조작됨) 시 401")
        void getMyInfo_InvalidToken_Returns401() {
            // given: 조작된 JWT 서명
            String manipulatedToken = "eyJhbGciOiJIUzI1NiJ9.invalid.signature";

            // when & then: @PreAuthorize("@access.authenticated()") 검증 실패
            given().spec(givenUnauthenticated())
                    .header("Authorization", "Bearer " + manipulatedToken)
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
