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
 * Auth 통합 플로우 E2E 테스트.
 *
 * <p>여러 API를 순차적으로 호출하여 전체 인증 시나리오를 검증합니다.
 *
 * <p>테스트 시나리오: - 로그인 → 내 정보 조회 → 로그아웃 전체 플로우
 *
 * <p>우선순위: - P0: 1개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("auth")
@Tag("flow")
@DisplayName("Auth Flow API E2E 테스트")
class AuthFlowE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/auth";

    @BeforeEach
    void setUp() {
        // AuthHub Mock 설정 (실제 구현 시 추가)
    }

    @Nested
    @DisplayName("전체 인증 플로우")
    class FullAuthenticationFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-AUTH-018] 로그인 → 내 정보 조회 → 로그아웃 전체 플로우")
        void fullAuthenticationFlow_Success() {
            // Step 1: POST /admin/auth/login - 로그인
            Map<String, Object> loginRequest =
                    Map.of(
                            "identifier", "admin@example.com",
                            "password", "password123!");

            Response loginResponse =
                    given().spec(givenAdminJson())
                            .body(loginRequest)
                            .when()
                            .post(BASE_URL + "/login");

            // 로그인 검증
            loginResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.accessToken", notNullValue())
                    .body("data.refreshToken", notNullValue())
                    .body("data.tokenType", equalTo("Bearer"))
                    .body("data.expiresIn", equalTo(3600));

            String accessToken = loginResponse.jsonPath().getString("data.accessToken");
            String refreshToken = loginResponse.jsonPath().getString("data.refreshToken");

            assertThat(accessToken).isNotBlank();
            assertThat(refreshToken).isNotBlank();

            // Step 2: GET /admin/auth/me - 내 정보 조회 (발급받은 토큰 사용)
            Response myInfoResponse =
                    given().spec(givenAdmin())
                            .header("Authorization", "Bearer " + accessToken)
                            .when()
                            .get(BASE_URL + "/me");

            // 내 정보 조회 검증
            myInfoResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.userId", notNullValue())
                    .body("data.email", equalTo("admin@example.com"))
                    .body("data.name", notNullValue())
                    .body("data.roles", notNullValue())
                    .body("data.permissions", notNullValue());

            String userId = myInfoResponse.jsonPath().getString("data.userId");
            assertThat(userId).isNotBlank();

            // Step 3: POST /admin/auth/logout - 로그아웃
            Response logoutResponse =
                    given().spec(givenAdmin())
                            .header("Authorization", "Bearer " + accessToken)
                            .when()
                            .post(BASE_URL + "/logout");

            // 로그아웃 검증
            logoutResponse.then().statusCode(HttpStatus.OK.value()).body("data", nullValue());

            // 전체 플로우 검증 완료
            // Note: 토큰 무효화는 Gateway 레벨에서 처리되므로 서비스 레벨 E2E 테스트에서 검증 불가
            assertThat(accessToken).as("Access Token이 발급되었음").isNotBlank();
            assertThat(userId).as("사용자 ID가 조회되었음").isNotBlank();
        }
    }

    @Nested
    @DisplayName("인증 실패 후 재시도 플로우")
    class FailedAuthenticationRetryFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("로그인 실패 → 재시도 → 성공")
        void loginFailedThenRetrySuccess_Flow() {
            // Step 1: 첫 번째 로그인 시도 (실패)
            Map<String, Object> failedLoginRequest =
                    Map.of(
                            "identifier", "admin@example.com",
                            "password", "wrongPassword123!");

            Response failedLoginResponse =
                    given().spec(givenAdminJson())
                            .body(failedLoginRequest)
                            .when()
                            .post(BASE_URL + "/login");

            // 실패 검증 (AuthCommandApiMapper에서 IllegalArgumentException → 400)
            failedLoginResponse.then().statusCode(HttpStatus.BAD_REQUEST.value());

            // Step 2: 두 번째 로그인 시도 (성공)
            Map<String, Object> successLoginRequest =
                    Map.of(
                            "identifier", "admin@example.com",
                            "password", "password123!");

            Response successLoginResponse =
                    given().spec(givenAdminJson())
                            .body(successLoginRequest)
                            .when()
                            .post(BASE_URL + "/login");

            // 성공 검증
            successLoginResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.accessToken", notNullValue());

            String accessToken = successLoginResponse.jsonPath().getString("data.accessToken");

            // Step 3: 내 정보 조회로 최종 확인
            given().spec(givenAdmin())
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.email", equalTo("admin@example.com"));
        }
    }

    @Nested
    @DisplayName("다중 로그인 플로우")
    class MultipleLoginFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("동일 사용자 중복 로그인 → 각 토큰 독립 동작")
        void multipleLoginsSameUser_IndependentTokens() {
            // Step 1: 첫 번째 로그인
            Map<String, Object> loginRequest =
                    Map.of(
                            "identifier", "admin@example.com",
                            "password", "password123!");

            Response firstLoginResponse =
                    given().spec(givenAdminJson())
                            .body(loginRequest)
                            .when()
                            .post(BASE_URL + "/login");

            firstLoginResponse.then().statusCode(HttpStatus.OK.value());
            String firstAccessToken = firstLoginResponse.jsonPath().getString("data.accessToken");

            // Step 2: 두 번째 로그인 (동일 사용자)
            Response secondLoginResponse =
                    given().spec(givenAdminJson())
                            .body(loginRequest)
                            .when()
                            .post(BASE_URL + "/login");

            secondLoginResponse.then().statusCode(HttpStatus.OK.value());
            String secondAccessToken = secondLoginResponse.jsonPath().getString("data.accessToken");

            // 두 토큰이 다른지 확인
            assertThat(firstAccessToken).isNotEqualTo(secondAccessToken);

            // Step 3: 첫 번째 토큰으로 내 정보 조회
            given().spec(givenAdmin())
                    .header("Authorization", "Bearer " + firstAccessToken)
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 4: 두 번째 토큰으로 내 정보 조회
            given().spec(givenAdmin())
                    .header("Authorization", "Bearer " + secondAccessToken)
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 5: 첫 번째 토큰 로그아웃
            given().spec(givenAdmin())
                    .header("Authorization", "Bearer " + firstAccessToken)
                    .when()
                    .post(BASE_URL + "/logout")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Note: 토큰 무효화는 Gateway 레벨에서 처리되므로 서비스 레벨 E2E 테스트에서 검증 불가
            // 두 번째 토큰으로 내 정보 조회 (여전히 유효)
            given().spec(givenAdmin())
                    .header("Authorization", "Bearer " + secondAccessToken)
                    .when()
                    .get(BASE_URL + "/me")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }
}
