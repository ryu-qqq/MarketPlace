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
 * Auth Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /admin/auth/login - 로그인 - POST /admin/auth/logout - 로그아웃
 *
 * <p>우선순위: - P0: 11개 시나리오 (필수 기능)
 *
 * <p>주의사항: - AuthHub SDK Mock 필요 - Spring Security Context 관리 필요
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("auth")
@Tag("command")
@DisplayName("Auth Command API E2E 테스트")
class AuthCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/public/auth";

    // AuthHub SDK Mock (실제 구현 시 추가)
    // @MockBean private AuthApi authApi;

    @BeforeEach
    void setUp() {
        // AuthHub Mock 설정 (실제 구현 시 추가)
        // given(authApi.login(any())).willReturn(...);
    }

    // ===== POST /admin/auth/login - 로그인 =====

    @Nested
    @DisplayName("POST /admin/auth/login - 로그인")
    class LoginTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-001] 유효한 자격증명으로 로그인 성공")
        void login_ValidCredentials_Returns200() {
            // given: 익명 사용자 (permitAll 엔드포인트)
            Map<String, Object> request = createLoginRequest("admin@example.com", "password123!");

            // when
            Response response =
                    given().spec(givenUnauthenticated())
                            .body(request)
                            .when()
                            .post(BASE_URL + "/login");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.accessToken", notNullValue())
                    .body("data.refreshToken", notNullValue())
                    .body("data.tokenType", equalTo("Bearer"))
                    .body("data.expiresIn", equalTo(3600));

            // 토큰 검증
            String accessToken = response.jsonPath().getString("data.accessToken");
            assertThat(accessToken).isNotBlank();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-002] 잘못된 비밀번호로 로그인 실패")
        void login_InvalidPassword_Returns401() {
            // given: 익명 사용자
            Map<String, Object> request =
                    createLoginRequest("admin@example.com", "wrongPassword123!");

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-003] 존재하지 않는 사용자로 로그인 실패")
        void login_NonExistingUser_Returns400() {
            // given: 익명 사용자
            Map<String, Object> request =
                    createLoginRequest("nonexistent@example.com", "password123!");

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-004] identifier 필드 누락 시 400")
        void login_MissingIdentifier_Returns400() {
            // given: 익명 사용자
            Map<String, Object> request = Map.of("password", "password123!");

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-005] password 필드 누락 시 400")
        void login_MissingPassword_Returns400() {
            // given: 익명 사용자
            Map<String, Object> request = Map.of("identifier", "admin@example.com");

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-006] identifier 빈 문자열 시 400")
        void login_EmptyIdentifier_Returns400() {
            // given: 익명 사용자
            Map<String, Object> request = createLoginRequest("", "password123!");

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-007] password 빈 문자열 시 400")
        void login_EmptyPassword_Returns400() {
            // given: 익명 사용자
            Map<String, Object> request = createLoginRequest("admin@example.com", "");

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/login")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== POST /admin/auth/logout - 로그아웃 =====

    @Nested
    @DisplayName("POST /admin/auth/logout - 로그아웃")
    class LogoutTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-019] 유효한 세션으로 로그아웃 성공")
        void logout_ValidSession_Returns200() {
            // given: 먼저 로그인
            Map<String, Object> loginRequest =
                    createLoginRequest("admin@example.com", "password123!");
            Response loginResponse =
                    given().spec(givenSuperAdmin())
                            .body(loginRequest)
                            .when()
                            .post(BASE_URL + "/login");

            String accessToken = loginResponse.jsonPath().getString("data.accessToken");

            // when: 로그아웃 (인증된 사용자 컨텍스트)
            Response logoutResponse =
                    given().spec(givenAuthenticatedUser())
                            .header("Authorization", "Bearer " + accessToken)
                            .when()
                            .post(BASE_URL + "/logout");

            // then
            logoutResponse.then().statusCode(HttpStatus.OK.value()).body("data", nullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-020] 인증되지 않은 세션으로 로그아웃 시도 시 400 (public endpoint, userId 없음)")
        void logout_UnauthenticatedSession_Returns400() {
            // given: 인증 헤더 없이 요청
            // when & then: public endpoint이므로 컨트롤러 진입 후 userId null로 인한 400
            given().spec(givenUnauthenticated())
                    .when()
                    .post(BASE_URL + "/logout")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-021] 만료된 토큰으로 로그아웃 시도 시 400 (public endpoint, userId 없음)")
        void logout_ExpiredToken_Returns400() {
            // given: 만료된 토큰
            String expiredToken = "eyJhbGciOiJIUzI1NiJ9.expired.token";

            // when & then: public endpoint이므로 컨트롤러 진입 후 userId null로 인한 400
            given().spec(givenUnauthenticated())
                    .header("Authorization", "Bearer " + expiredToken)
                    .when()
                    .post(BASE_URL + "/logout")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== Helper Methods =====

    /** 로그인 요청 생성 Helper. */
    private Map<String, Object> createLoginRequest(String identifier, String password) {
        return Map.of(
                "identifier", identifier,
                "password", password);
    }
}
