package com.ryuqq.marketplace.integration.shop;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Shop Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /shops - 외부몰 등록 (201) - PUT /shops/{shopId} - 외부몰 수정 (204)
 *
 * <p>Phase 3 (MEDIUM): superAdmin 전용 커맨드, shop:write 권한 필요
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("e2e")
@Tag("shop")
@Tag("command")
@DisplayName("[E2E] Shop Command API 테스트")
class ShopCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/shops";
    private static final Long SALES_CHANNEL_ID = 1L;

    @Autowired private ShopJpaRepository shopRepository;

    @BeforeEach
    void setUp() {
        shopRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        shopRepository.deleteAll();
    }

    private static Map<String, Object> registerRequest(String shopName, String accountId) {
        Map<String, Object> request = new HashMap<>();
        request.put("salesChannelId", SALES_CHANNEL_ID);
        request.put("shopName", shopName);
        request.put("accountId", accountId);
        return request;
    }

    private static Map<String, Object> updateRequest(
            String shopName, String accountId, String status) {
        Map<String, Object> request = new HashMap<>();
        request.put("shopName", shopName);
        request.put("accountId", accountId);
        request.put("status", status);
        return request;
    }

    // ===== POST /shops - 외부몰 등록 =====

    @Nested
    @DisplayName("POST /shops - 외부몰 등록")
    class RegisterShopTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] SuperAdmin 외부몰 등록 → 201 Created")
        void registerShop_superAdmin_returns201WithShopId() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(registerRequest("테스트외부몰", "test-account-001"))
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.shopId", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] 비인증 사용자 → 401 Unauthorized")
        void registerShop_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .body(registerRequest("비인증몰", "unauth-account"))
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-3] 필수 필드 누락 (shopName 빈값) → 400 Bad Request")
        void registerShop_blankShopName_returns400() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(registerRequest("", "valid-account"))
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-4] 동일 판매채널 내 중복 accountId 등록 → 409 Conflict")
        void registerShop_duplicateAccountId_returns409() {
            // given: 첫 번째 등록
            given().spec(givenSuperAdmin())
                    .body(registerRequest("외부몰A", "duplicate-account"))
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // when & then: 동일 accountId로 두 번째 등록 시도
            given().spec(givenSuperAdmin())
                    .body(registerRequest("외부몰B", "duplicate-account"))
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    // ===== PUT /shops/{shopId} - 외부몰 수정 =====

    @Nested
    @DisplayName("PUT /shops/{shopId} - 외부몰 수정")
    class UpdateShopTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] SuperAdmin 외부몰 수정 → 204 No Content")
        void updateShop_superAdmin_returns204() {
            // given: 등록
            Long shopId =
                    given().spec(givenSuperAdmin())
                            .body(registerRequest("수정대상몰", "update-target-account"))
                            .when()
                            .post(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract()
                            .jsonPath()
                            .getLong("data.shopId");

            // when & then: 수정
            given().spec(givenSuperAdmin())
                    .body(updateRequest("수정된몰명", "updated-account", "ACTIVE"))
                    .when()
                    .put(BASE_URL + "/{shopId}", shopId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] 존재하지 않는 shopId 수정 → 404 Not Found")
        void updateShop_nonExistingShopId_returns404() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(updateRequest("없는몰", "no-account", "ACTIVE"))
                    .when()
                    .put(BASE_URL + "/{shopId}", 99999)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-3] 비인증 사용자 → 401 Unauthorized")
        void updateShop_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .body(updateRequest("몰명", "account", "ACTIVE"))
                    .when()
                    .put(BASE_URL + "/{shopId}", 1)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-4] 잘못된 status 값 → 400 Bad Request")
        void updateShop_invalidStatus_returns400() {
            // given: 등록
            Long shopId =
                    given().spec(givenSuperAdmin())
                            .body(registerRequest("상태검증몰", "status-check-account"))
                            .when()
                            .post(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract()
                            .jsonPath()
                            .getLong("data.shopId");

            // when & then: 잘못된 status 값
            given().spec(givenSuperAdmin())
                    .body(updateRequest("상태검증몰", "status-check-account", "INVALID_STATUS"))
                    .when()
                    .put(BASE_URL + "/{shopId}", shopId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== 전체 플로우 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 등록 → 수정 → 조회 (변경 반영 확인)")
        void fullFlow_register_update_query() {
            // Step 1: 외부몰 등록
            Long shopId =
                    given().spec(givenSuperAdmin())
                            .body(registerRequest("플로우몰", "flow-account"))
                            .when()
                            .post(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .body("data.shopId", notNullValue())
                            .extract()
                            .jsonPath()
                            .getLong("data.shopId");

            // Step 2: 외부몰 수정 (이름 + 계정 변경, INACTIVE 처리)
            given().spec(givenSuperAdmin())
                    .body(updateRequest("플로우몰-수정", "flow-account-updated", "INACTIVE"))
                    .when()
                    .put(BASE_URL + "/{shopId}", shopId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 3: 조회로 변경 반영 확인
            given().spec(givenSuperAdmin())
                    .queryParam("searchField", "SHOP_NAME")
                    .queryParam("searchWord", "플로우몰-수정")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].shopName", equalTo("플로우몰-수정"))
                    .body("data.content[0].accountId", equalTo("flow-account-updated"))
                    .body("data.content[0].status", equalTo("INACTIVE"));
        }
    }
}
