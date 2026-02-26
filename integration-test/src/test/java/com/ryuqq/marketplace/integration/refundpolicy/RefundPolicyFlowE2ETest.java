package com.ryuqq.marketplace.integration.refundpolicy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository.RefundPolicyJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * RefundPolicy 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상: - CRUD 전체 플로우 시나리오 - 기본 정책 전이 플로우 시나리오
 *
 * <p>우선순위: - P0: 2개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("refundpolicy")
@Tag("flow")
@DisplayName("RefundPolicy Flow E2E 테스트")
class RefundPolicyFlowE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sellers/{sellerId}/refund-policies";
    private static final Long SELLER_ID = 1L;

    @Autowired private RefundPolicyJpaRepository refundPolicyRepository;

    @BeforeEach
    void setUp() {
        refundPolicyRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        refundPolicyRepository.deleteAll();
    }

    @Test
    @Tag("P0")
    @DisplayName("[FLOW-1] 생성 → 조회 → 수정 → 상태 변경 전체 플로우")
    void fullCrudFlow_AllOperations_Success() {
        // ===== Step 1: POST - 정책 생성 =====
        Map<String, Object> createRequest =
                Map.of(
                        "policyName",
                        "테스트 정책",
                        "defaultPolicy",
                        false,
                        "returnPeriodDays",
                        7,
                        "exchangePeriodDays",
                        14,
                        "nonReturnableConditions",
                        List.of("OPENED_PACKAGING"),
                        "partialRefundEnabled",
                        true,
                        "inspectionRequired",
                        true,
                        "inspectionPeriodDays",
                        3,
                        "additionalInfo",
                        "테스트 추가 안내");

        Response createResponse =
                given().spec(givenAdminJson()).body(createRequest).when().post(BASE_URL, SELLER_ID);

        createResponse.then().statusCode(HttpStatus.CREATED.value());
        Long policyId = createResponse.jsonPath().getLong("data.policyId");

        // ===== Step 2: GET - 목록 조회 (생성 확인) =====
        given().spec(givenAdmin())
                .when()
                .get(BASE_URL, SELLER_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.content.size()", equalTo(1))
                .body("data.content[0].policyId", equalTo(policyId.intValue()))
                .body("data.content[0].policyName", equalTo("테스트 정책"));

        // ===== Step 3: PUT - 정책 수정 =====
        // defaultPolicy=true 유지 (유일한 기본 정책 해제 불가)
        Map<String, Object> updateRequest =
                Map.of(
                        "policyName",
                        "수정된 정책",
                        "defaultPolicy",
                        true,
                        "returnPeriodDays",
                        14,
                        "exchangePeriodDays",
                        30,
                        "nonReturnableConditions",
                        List.of("USED_PRODUCT"),
                        "partialRefundEnabled",
                        false,
                        "inspectionRequired",
                        false,
                        "inspectionPeriodDays",
                        0,
                        "additionalInfo",
                        "수정된 안내");

        given().spec(givenAdminJson())
                .body(updateRequest)
                .when()
                .put(BASE_URL + "/{policyId}", SELLER_ID, policyId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // ===== Step 4: GET - 목록 조회 (수정 확인) =====
        given().spec(givenAdmin())
                .when()
                .get(BASE_URL, SELLER_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.content[0].policyName", equalTo("수정된 정책"))
                .body("data.content[0].returnPeriodDays", equalTo(14));

        // ===== Step 5: POST - 추가 정책 생성 (기본 정책으로 지정하여 첫 번째 정책에서 기본 해제) =====
        Map<String, Object> secondCreateRequest =
                Map.of(
                        "policyName",
                        "두 번째 정책",
                        "defaultPolicy",
                        true,
                        "returnPeriodDays",
                        30,
                        "exchangePeriodDays",
                        30);

        Response secondCreateResponse =
                given().spec(givenAdminJson())
                        .body(secondCreateRequest)
                        .when()
                        .post(BASE_URL, SELLER_ID);

        secondCreateResponse.then().statusCode(HttpStatus.CREATED.value());
        Long secondPolicyId = secondCreateResponse.jsonPath().getLong("data.policyId");

        // ===== Step 6: PATCH - 첫 번째 정책 비활성화 =====
        Map<String, Object> statusChangeRequest =
                Map.of("policyIds", List.of(policyId), "active", false);

        given().spec(givenAdminJson())
                .body(statusChangeRequest)
                .when()
                .patch(BASE_URL + "/status", SELLER_ID)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // ===== Step 7: GET - 목록 조회 (상태 변경 확인) =====
        given().spec(givenAdmin())
                .when()
                .get(BASE_URL, SELLER_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.content.size()", equalTo(2))
                .body(
                        "data.content.find { it.policyId == " + secondPolicyId + " }.active",
                        equalTo(true))
                .body(
                        "data.content.find { it.policyId == " + policyId + " }.active",
                        equalTo(false));
    }

    @Test
    @Tag("P0")
    @DisplayName("[FLOW-2] 기본 정책 생성 → 기본 정책 전환 → 검증")
    void defaultPolicyTransferFlow_AllOperations_Success() {
        // ===== Step 1: POST - 첫 번째 정책 생성 (defaultPolicy=false) =====
        Map<String, Object> firstRequest =
                Map.of(
                        "policyName",
                        "정책1",
                        "defaultPolicy",
                        false,
                        "returnPeriodDays",
                        7,
                        "exchangePeriodDays",
                        14);

        Response firstResponse =
                given().spec(givenAdminJson()).body(firstRequest).when().post(BASE_URL, SELLER_ID);

        firstResponse.then().statusCode(HttpStatus.CREATED.value());
        Long firstPolicyId = firstResponse.jsonPath().getLong("data.policyId");

        // ===== Step 2: GET - 첫 번째 정책이 자동으로 기본 정책이 되었는지 확인 =====
        given().spec(givenAdmin())
                .when()
                .get(BASE_URL, SELLER_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.content[0].defaultPolicy", equalTo(true));

        // ===== Step 3: POST - 두 번째 정책 생성 (기본 정책 지정) =====
        Map<String, Object> secondRequest =
                Map.of(
                        "policyName",
                        "정책2",
                        "defaultPolicy",
                        true,
                        "returnPeriodDays",
                        14,
                        "exchangePeriodDays",
                        30);

        Response secondResponse =
                given().spec(givenAdminJson()).body(secondRequest).when().post(BASE_URL, SELLER_ID);

        secondResponse.then().statusCode(HttpStatus.CREATED.value());
        Long secondPolicyId = secondResponse.jsonPath().getLong("data.policyId");

        // ===== Step 4: GET - 기본 정책 전환 확인 =====
        given().spec(givenAdmin())
                .when()
                .get(BASE_URL, SELLER_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(
                        "data.content.find { it.policyId == " + firstPolicyId + " }.defaultPolicy",
                        equalTo(false))
                .body(
                        "data.content.find { it.policyId == " + secondPolicyId + " }.defaultPolicy",
                        equalTo(true));

        // ===== Step 5: PUT - 정책1을 다시 기본 정책으로 변경 =====
        Map<String, Object> updateRequest =
                Map.of(
                        "policyName",
                        "정책1",
                        "defaultPolicy",
                        true,
                        "returnPeriodDays",
                        7,
                        "exchangePeriodDays",
                        14);

        given().spec(givenAdminJson())
                .body(updateRequest)
                .when()
                .put(BASE_URL + "/{policyId}", SELLER_ID, firstPolicyId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // ===== Step 6: GET - 기본 정책 재전환 확인 =====
        given().spec(givenAdmin())
                .when()
                .get(BASE_URL, SELLER_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(
                        "data.content.find { it.policyId == " + firstPolicyId + " }.defaultPolicy",
                        equalTo(true))
                .body(
                        "data.content.find { it.policyId == " + secondPolicyId + " }.defaultPolicy",
                        equalTo(false));
    }
}
