package com.ryuqq.marketplace.integration.shippingpolicy;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository.ShippingPolicyJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.List;
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
 * ShippingPolicy 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상: - CRUD 전체 플로우 - 기본 정책 변경 플로우
 *
 * <p>우선순위: - P0: 2개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("shippingpolicy")
@Tag("flow")
@DisplayName("ShippingPolicy Flow E2E 테스트")
class ShippingPolicyFlowE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sellers/{sellerId}/shipping-policies";
    private static final Long DEFAULT_SELLER_ID = 1L;

    @Autowired private ShippingPolicyJpaRepository shippingPolicyRepository;

    @BeforeEach
    void setUp() {
        shippingPolicyRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        shippingPolicyRepository.deleteAll();
    }

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowScenarios {

        @Test
        @Tag("P0")
        @DisplayName("[TC-F1] CRUD 전체 플로우")
        void fullCrudFlow_Success() {
            // 1. POST - 배송정책 생성
            Map<String, Object> createRequest = createRegisterRequest("기본 배송정책", true);

            Response createResponse =
                    given().spec(givenAdminJson())
                            .body(createRequest)
                            .when()
                            .post(BASE_URL, DEFAULT_SELLER_ID);

            createResponse.then().statusCode(HttpStatus.CREATED.value());

            Long policyId = createResponse.jsonPath().getLong("data.policyId");
            assertThat(policyId).isNotNull();

            // 2. GET - 목록 조회로 생성 확인
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].policyId", equalTo(policyId.intValue()))
                    .body("data.content[0].policyName", equalTo("기본 배송정책"));

            // 3. PUT - 정책 수정
            Map<String, Object> updateRequest = createUpdateRequest("수정된 배송정책", true, 5000);

            given().spec(givenAdminJson())
                    .body(updateRequest)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, policyId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 4. GET - 목록 조회로 수정 확인
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].policyName", equalTo("수정된 배송정책"))
                    .body("data.content[0].baseFee", equalTo(5000));

            // 5. PATCH - 비활성화 (기본 정책이므로 실패할 것 - 추가 정책 먼저 생성)
            Map<String, Object> additionalRequest = createRegisterRequest("추가 정책", false);
            given().spec(givenAdminJson())
                    .body(additionalRequest)
                    .post(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // 두 번째 정책을 기본 정책으로 변경
            var policies = shippingPolicyRepository.findAll();
            Long additionalPolicyId =
                    policies.stream()
                            .filter(p -> p.getPolicyName().equals("추가 정책"))
                            .findFirst()
                            .orElseThrow()
                            .getId();

            Map<String, Object> changeDefaultRequest = createUpdateRequest("추가 정책", true, 3000);
            given().spec(givenAdminJson())
                    .body(changeDefaultRequest)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, additionalPolicyId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 이제 첫 번째 정책 비활성화 가능
            Map<String, Object> statusRequest =
                    Map.of("policyIds", List.of(policyId), "active", false);

            given().spec(givenAdminJson())
                    .body(statusRequest)
                    .when()
                    .patch(BASE_URL + "/status", DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 6. GET - 목록 조회로 비활성화 확인
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body(
                            "data.content.find { it.policyId == "
                                    + policyId.intValue()
                                    + " }.active",
                            equalTo(false));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-F2] 기본 정책 변경 플로우")
        void defaultPolicyChangeFlow_Success() {
            // 1. POST - 첫 번째 정책 생성 (자동 기본 정책)
            Map<String, Object> firstRequest = createRegisterRequest("첫번째 정책", false);

            Response firstResponse =
                    given().spec(givenAdminJson())
                            .body(firstRequest)
                            .when()
                            .post(BASE_URL, DEFAULT_SELLER_ID);

            firstResponse.then().statusCode(HttpStatus.CREATED.value());
            Long firstPolicyId = firstResponse.jsonPath().getLong("data.policyId");

            // 2. GET - defaultPolicy=true 확인
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(
                            "data.content.find { it.policyId == "
                                    + firstPolicyId.intValue()
                                    + " }.defaultPolicy",
                            equalTo(true));

            // 3. POST - 두 번째 정책 생성 (defaultPolicy=false)
            Map<String, Object> secondRequest = createRegisterRequest("두번째 정책", false);

            Response secondResponse =
                    given().spec(givenAdminJson())
                            .body(secondRequest)
                            .when()
                            .post(BASE_URL, DEFAULT_SELLER_ID);

            secondResponse.then().statusCode(HttpStatus.CREATED.value());
            Long secondPolicyId = secondResponse.jsonPath().getLong("data.policyId");

            // 4. GET - 기본 정책은 여전히 1개
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content.findAll { it.defaultPolicy == true }.size()", equalTo(1));

            // 5. PUT - 두 번째 정책을 기본 정책으로 변경
            Map<String, Object> updateRequest = createUpdateRequest("두번째 정책 (기본)", true, 3000);

            given().spec(givenAdminJson())
                    .body(updateRequest)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, secondPolicyId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 6. GET - 기본 정책 변경 확인
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(
                            "data.content.find { it.policyId == "
                                    + firstPolicyId.intValue()
                                    + " }.defaultPolicy",
                            equalTo(false))
                    .body(
                            "data.content.find { it.policyId == "
                                    + secondPolicyId.intValue()
                                    + " }.defaultPolicy",
                            equalTo(true));
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createRegisterRequest(String policyName, Boolean defaultPolicy) {
        Map<String, Object> leadTime =
                Map.of(
                        "minDays", 1,
                        "maxDays", 3,
                        "cutoffTime", "14:00");

        return Map.of(
                "policyName", policyName,
                "defaultPolicy", defaultPolicy,
                "shippingFeeType", "CONDITIONAL_FREE",
                "baseFee", 3000,
                "freeThreshold", 50000,
                "jejuExtraFee", 3000,
                "islandExtraFee", 5000,
                "returnFee", 3000,
                "exchangeFee", 6000,
                "leadTime", leadTime);
    }

    private Map<String, Object> createUpdateRequest(
            String policyName, Boolean defaultPolicy, Integer baseFee) {
        Map<String, Object> leadTime =
                Map.of(
                        "minDays", 1,
                        "maxDays", 3,
                        "cutoffTime", "14:00");

        // Map.of()는 최대 10개 key-value만 지원하므로 HashMap 사용
        var request = new java.util.HashMap<String, Object>();
        request.put("policyName", policyName);
        request.put("defaultPolicy", defaultPolicy);
        request.put("shippingFeeType", "PAID");
        request.put("baseFee", baseFee);
        request.put("jejuExtraFee", 3000);
        request.put("islandExtraFee", 5000);
        request.put("returnFee", 3000);
        request.put("exchangeFee", 6000);
        request.put("leadTime", leadTime);
        return request;
    }
}
