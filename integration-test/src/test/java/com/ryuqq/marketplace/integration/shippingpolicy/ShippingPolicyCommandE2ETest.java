package com.ryuqq.marketplace.integration.shippingpolicy;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
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
 * ShippingPolicy Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /sellers/{sellerId}/shipping-policies - 배송정책 등록 - PUT
 * /sellers/{sellerId}/shipping-policies/{policyId} - 배송정책 수정 - PATCH
 * /sellers/{sellerId}/shipping-policies/status - 배송정책 상태 변경
 *
 * <p>우선순위: - P0: 20개 시나리오 (필수 기능) - P1: 8개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("shippingpolicy")
@Tag("command")
@DisplayName("ShippingPolicy Command API E2E 테스트")
class ShippingPolicyCommandE2ETest extends E2ETestBase {

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

    // ===== POST /sellers/{sellerId}/shipping-policies - 배송정책 등록 =====

    @Nested
    @DisplayName("POST /sellers/{sellerId}/shipping-policies - 배송정책 등록")
    class RegisterShippingPolicyTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-01] 생성 성공 - 첫 번째 정책 (자동 기본 정책)")
        void registerShippingPolicy_FirstPolicy_AutoDefault_Returns201() {
            // given
            Map<String, Object> request = createRegisterRequest("기본 배송정책", false);

            // when
            Response response =
                    given().spec(givenAdminJson())
                            .body(request)
                            .when()
                            .post(BASE_URL, DEFAULT_SELLER_ID);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long policyId = response.jsonPath().getLong("data.policyId");
            assertThat(policyId).isNotNull();

            // DB 검증: 첫 번째 정책은 자동으로 기본 정책으로 설정
            var savedPolicy = shippingPolicyRepository.findById(policyId).orElseThrow();
            assertThat(savedPolicy.isDefaultPolicy()).isTrue();
            assertThat(savedPolicy.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-02] 생성 성공 - 기본 정책 명시 등록 (기존 기본 정책 해제)")
        void registerShippingPolicy_NewDefault_ExistingDefaultUnmarked_Returns201() {
            // given
            var existingDefault =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newDefaultEntity(DEFAULT_SELLER_ID));
            Long existingDefaultId = existingDefault.getId();

            Map<String, Object> request = createRegisterRequest("새 기본 정책", true);

            // when
            Response response =
                    given().spec(givenAdminJson())
                            .body(request)
                            .when()
                            .post(BASE_URL, DEFAULT_SELLER_ID);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long newPolicyId = response.jsonPath().getLong("data.policyId");

            // DB 검증: 기존 기본 정책 해제, 새 정책이 기본 정책으로 설정
            var oldDefault = shippingPolicyRepository.findById(existingDefaultId).orElseThrow();
            assertThat(oldDefault.isDefaultPolicy()).isFalse();

            var newDefault = shippingPolicyRepository.findById(newPolicyId).orElseThrow();
            assertThat(newDefault.isDefaultPolicy()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-03] 생성 성공 - 비기본 정책 등록")
        void registerShippingPolicy_NonDefault_Returns201() {
            // given
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newDefaultEntity(
                            DEFAULT_SELLER_ID)); // 기본 정책 존재

            Map<String, Object> request = createRegisterRequest("추가 배송정책", false);

            // when
            Response response =
                    given().spec(givenAdminJson())
                            .body(request)
                            .when()
                            .post(BASE_URL, DEFAULT_SELLER_ID);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long policyId = response.jsonPath().getLong("data.policyId");

            // DB 검증
            var savedPolicy = shippingPolicyRepository.findById(policyId).orElseThrow();
            assertThat(savedPolicy.isDefaultPolicy()).isFalse();
            assertThat(savedPolicy.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-04] 필수 필드 누락 - policyName")
        void registerShippingPolicy_MissingPolicyName_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest("", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-05] 필수 필드 누락 - defaultPolicy")
        void registerShippingPolicy_MissingDefaultPolicy_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest("정책명", null);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-06] 필수 필드 누락 - shippingFeeType")
        void registerShippingPolicy_MissingShippingFeeType_Returns400() {
            // given
            Map<String, Object> request = Map.of("policyName", "정책명", "defaultPolicy", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-07] 잘못된 shippingFeeType 값")
        void registerShippingPolicy_InvalidShippingFeeType_Returns400() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "policyName",
                            "정책명",
                            "defaultPolicy",
                            true,
                            "shippingFeeType",
                            "INVALID_TYPE",
                            "baseFee",
                            3000);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-08] POL-FEE-001 위반 - CONDITIONAL_FREE에 freeThreshold 누락")
        void registerShippingPolicy_ConditionalFreeWithoutThreshold_Returns400() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "policyName",
                            "정책명",
                            "defaultPolicy",
                            true,
                            "shippingFeeType",
                            "CONDITIONAL_FREE",
                            "baseFee",
                            3000);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PUT /sellers/{sellerId}/shipping-policies/{policyId} - 배송정책 수정 =====

    @Nested
    @DisplayName("PUT /sellers/{sellerId}/shipping-policies/{policyId} - 배송정책 수정")
    class UpdateShippingPolicyTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-01] 수정 성공 - 정책명 및 배송비 변경")
        void updateShippingPolicy_NameAndFee_Returns204() {
            // given
            var policy =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newDefaultEntity(DEFAULT_SELLER_ID));
            Long policyId = policy.getId();

            // 유일한 기본 정책 해제 불가 -> defaultPolicy=true 유지
            Map<String, Object> request = createUpdateRequest("수정된 정책", true, 5000);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, policyId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var updated = shippingPolicyRepository.findById(policyId).orElseThrow();
            assertThat(updated.getPolicyName()).isEqualTo("수정된 정책");
            assertThat(updated.getBaseFee()).isEqualTo(5000);
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-02] 수정 성공 - 비기본 정책 → 기본 정책 변경")
        void updateShippingPolicy_NonDefaultToDefault_Returns204() {
            // given
            var existingDefault =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newDefaultEntity(DEFAULT_SELLER_ID));
            var nonDefault =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "정책2"));

            Long existingDefaultId = existingDefault.getId();
            Long nonDefaultId = nonDefault.getId();

            Map<String, Object> request = createUpdateRequest("새 기본 정책", true, 3000);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, nonDefaultId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var oldDefault = shippingPolicyRepository.findById(existingDefaultId).orElseThrow();
            assertThat(oldDefault.isDefaultPolicy()).isFalse();

            var newDefault = shippingPolicyRepository.findById(nonDefaultId).orElseThrow();
            assertThat(newDefault.isDefaultPolicy()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-03] 수정 성공 - shippingFeeType 변경 (CONDITIONAL_FREE → FREE)")
        void updateShippingPolicy_FeeTypeChange_Returns204() {
            // given
            var policy =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity());
            Long policyId = policy.getId();

            Map<String, Object> leadTime =
                    Map.of(
                            "minDays", 1,
                            "maxDays", 3,
                            "cutoffTime", "14:00");

            Map<String, Object> request =
                    Map.of(
                            "policyName",
                            "무료배송으로 변경",
                            "defaultPolicy",
                            true,
                            "shippingFeeType",
                            "FREE",
                            "baseFee",
                            0,
                            "leadTime",
                            leadTime);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, policyId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var updated = shippingPolicyRepository.findById(policyId).orElseThrow();
            assertThat(updated.getShippingFeeType()).isEqualTo("FREE");
            // H2에서 nullable Long 컬럼이 0으로 반환될 수 있음
            assertThat(updated.getFreeThreshold())
                    .satisfiesAnyOf(v -> assertThat(v).isNull(), v -> assertThat(v).isEqualTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-04] 존재하지 않는 정책 수정 시도")
        void updateShippingPolicy_NonExistingId_Returns404() {
            // given
            Long nonExistingId = 99999L;
            Map<String, Object> request = createUpdateRequest("정책명", false, 3000);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-05] 다른 셀러의 정책 수정 시도")
        void updateShippingPolicy_DifferentSeller_Returns404() {
            // given
            Long differentSellerId = 2L;
            var policy =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newDefaultEntity(
                                    differentSellerId)); // 셀러 ID=2
            Long policyId = policy.getId();

            Map<String, Object> request = createUpdateRequest("정책명", false, 3000);

            // when & then (셀러 ID=1로 요청)
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, policyId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-06] POL-FEE-001 위반 - CONDITIONAL_FREE로 변경하면서 freeThreshold 누락")
        void updateShippingPolicy_ConditionalFreeWithoutThreshold_Returns400() {
            // given
            var policy =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.freeShippingEntity());
            Long policyId = policy.getId();

            Map<String, Object> request =
                    Map.of(
                            "policyName",
                            "조건부 무료로 변경",
                            "defaultPolicy",
                            true,
                            "shippingFeeType",
                            "CONDITIONAL_FREE",
                            "baseFee",
                            3000);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", DEFAULT_SELLER_ID, policyId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PATCH /sellers/{sellerId}/shipping-policies/status - 배송정책 상태 변경 =====

    @Nested
    @DisplayName("PATCH /sellers/{sellerId}/shipping-policies/status - 배송정책 상태 변경")
    class ChangeShippingPolicyStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-01] 활성화 성공 - 비활성 정책 활성화")
        void changeStatus_Activate_Returns204() {
            // given
            var policy1 =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newInactiveEntity(DEFAULT_SELLER_ID));
            var policy2 =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newInactiveEntity(DEFAULT_SELLER_ID));

            List<Long> policyIds = List.of(policy1.getId(), policy2.getId());

            Map<String, Object> request = Map.of("policyIds", policyIds, "active", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var updated1 = shippingPolicyRepository.findById(policy1.getId()).orElseThrow();
            var updated2 = shippingPolicyRepository.findById(policy2.getId()).orElseThrow();
            assertThat(updated1.isActive()).isTrue();
            assertThat(updated2.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-02] 비활성화 성공 - 비기본 정책 비활성화")
        void changeStatus_DeactivateNonDefault_Returns204() {
            // given
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newDefaultEntity(
                            DEFAULT_SELLER_ID)); // 기본 정책 (ID=1)
            var policy2 =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "정책2"));
            var policy3 =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "정책3"));

            List<Long> policyIds = List.of(policy2.getId(), policy3.getId());

            Map<String, Object> request = Map.of("policyIds", policyIds, "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var updated2 = shippingPolicyRepository.findById(policy2.getId()).orElseThrow();
            var updated3 = shippingPolicyRepository.findById(policy3.getId()).orElseThrow();
            assertThat(updated2.isActive()).isFalse();
            assertThat(updated3.isActive()).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-03] POL-DEACT-001 위반 - 기본 정책 비활성화 시도")
        void changeStatus_DeactivateDefault_Returns400() {
            // given
            var defaultPolicy =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newDefaultEntity(DEFAULT_SELLER_ID));

            List<Long> policyIds = List.of(defaultPolicy.getId());

            Map<String, Object> request = Map.of("policyIds", policyIds, "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-04] POL-DEACT-002 위반 - 마지막 활성 정책 비활성화 시도")
        void changeStatus_DeactivateLastActive_Returns400() {
            // given
            var onlyActivePolicy =
                    shippingPolicyRepository.save(
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "유일한 활성 정책"));

            List<Long> policyIds = List.of(onlyActivePolicy.getId());

            Map<String, Object> request = Map.of("policyIds", policyIds, "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-05] 존재하지 않는 정책 상태 변경 시도")
        void changeStatus_NonExistingId_Returns404() {
            // given
            List<Long> policyIds = List.of(99999L);

            Map<String, Object> request = Map.of("policyIds", policyIds, "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-06] 빈 policyIds 리스트")
        void changeStatus_EmptyPolicyIds_Returns400() {
            // given
            List<Long> policyIds = List.of();

            Map<String, Object> request = Map.of("policyIds", policyIds, "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createRegisterRequest(String policyName, Boolean defaultPolicy) {
        Map<String, Object> leadTime =
                Map.of(
                        "minDays", 1,
                        "maxDays", 3,
                        "cutoffTime", "14:00");

        if (defaultPolicy == null) {
            return Map.of(
                    "policyName", policyName,
                    "shippingFeeType", "CONDITIONAL_FREE",
                    "baseFee", 3000,
                    "freeThreshold", 50000,
                    "jejuExtraFee", 3000,
                    "islandExtraFee", 5000,
                    "returnFee", 3000,
                    "exchangeFee", 6000,
                    "leadTime", leadTime);
        }

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
