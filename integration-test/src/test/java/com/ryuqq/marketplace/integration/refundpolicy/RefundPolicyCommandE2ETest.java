package com.ryuqq.marketplace.integration.refundpolicy;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository.RefundPolicyJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.HashMap;
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
 * RefundPolicy Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /sellers/{sellerId}/refund-policies - 환불정책 등록 - PUT
 * /sellers/{sellerId}/refund-policies/{policyId} - 환불정책 수정 - PATCH
 * /sellers/{sellerId}/refund-policies/status - 환불정책 상태 변경
 *
 * <p>우선순위: - P0: 12개 시나리오 (필수 기능) - P1: 9개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("refundpolicy")
@Tag("command")
@DisplayName("RefundPolicy Command API E2E 테스트")
class RefundPolicyCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sellers/{sellerId}/refund-policies";
    private static final Long SELLER_ID = 1L;
    private static final Long OTHER_SELLER_ID = 2L;

    @Autowired private RefundPolicyJpaRepository refundPolicyRepository;

    @BeforeEach
    void setUp() {
        refundPolicyRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        refundPolicyRepository.deleteAll();
    }

    // ===== POST /sellers/{sellerId}/refund-policies - 환불정책 등록 =====

    @Nested
    @DisplayName("POST /sellers/{sellerId}/refund-policies - 환불정책 등록")
    class RegisterRefundPolicyTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] 유효한 요청으로 정책 생성 (첫 번째 정책)")
        void registerRefundPolicy_FirstPolicy_Returns201() {
            // given
            Map<String, Object> request = createRegisterRequest(false);

            // when
            Response response =
                    given().spec(givenAdminJson()).body(request).when().post(BASE_URL, SELLER_ID);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long policyId = response.jsonPath().getLong("data.policyId");
            assertThat(policyId).isNotNull().isGreaterThan(0);

            // DB 검증: 첫 번째 정책이므로 자동으로 기본 정책 설정
            RefundPolicyJpaEntity saved = refundPolicyRepository.findById(policyId).orElseThrow();
            assertThat(saved.isDefaultPolicy()).isTrue();
            assertThat(saved.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] 유효한 요청으로 정책 생성 (두 번째 정책)")
        void registerRefundPolicy_SecondPolicy_Returns201() {
            // given
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            Map<String, Object> request = createRegisterRequest(false);

            // when
            Response response =
                    given().spec(givenAdminJson()).body(request).when().post(BASE_URL, SELLER_ID);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long policyId = response.jsonPath().getLong("data.policyId");
            assertThat(policyId).isNotNull();

            // DB 검증: 두 번째 정책이므로 기본 정책 아님
            RefundPolicyJpaEntity saved = refundPolicyRepository.findById(policyId).orElseThrow();
            assertThat(saved.isDefaultPolicy()).isFalse();
            assertThat(saved.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-3] 기본 정책 등록 시 기존 기본 정책 해제")
        void registerRefundPolicy_NewDefault_UnmarksOldDefault() {
            // given
            RefundPolicyJpaEntity oldDefault =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            Map<String, Object> request = createRegisterRequest(true);

            // when
            Response response =
                    given().spec(givenAdminJson()).body(request).when().post(BASE_URL, SELLER_ID);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long newPolicyId = response.jsonPath().getLong("data.policyId");

            // DB 검증: 새 정책은 기본 정책, 기존 정책은 기본 정책 해제
            RefundPolicyJpaEntity newPolicy =
                    refundPolicyRepository.findById(newPolicyId).orElseThrow();
            assertThat(newPolicy.isDefaultPolicy()).isTrue();

            RefundPolicyJpaEntity oldPolicy =
                    refundPolicyRepository.findById(oldDefault.getId()).orElseThrow();
            assertThat(oldPolicy.isDefaultPolicy()).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-4] 필수 필드 누락 시 400 에러 (policyName)")
        void registerRefundPolicy_MissingPolicyName_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest(false);
            request.put("policyName", "");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증: 데이터 생성 안 됨
            assertThat(refundPolicyRepository.count()).isZero();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-5] returnPeriodDays 범위 벗어남 (0일)")
        void registerRefundPolicy_InvalidReturnPeriodZero_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest(false);
            request.put("returnPeriodDays", 0);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증
            assertThat(refundPolicyRepository.count()).isZero();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-6] returnPeriodDays 범위 벗어남 (91일)")
        void registerRefundPolicy_InvalidReturnPeriodExceeded_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest(false);
            request.put("returnPeriodDays", 91);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증
            assertThat(refundPolicyRepository.count()).isZero();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-7] policyName 길이 초과 (101자)")
        void registerRefundPolicy_PolicyNameTooLong_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest(false);
            request.put("policyName", "A".repeat(101));

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증
            assertThat(refundPolicyRepository.count()).isZero();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-8] additionalInfo 길이 초과 (1001자)")
        void registerRefundPolicy_AdditionalInfoTooLong_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest(false);
            request.put("additionalInfo", "A".repeat(1001));

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증
            assertThat(refundPolicyRepository.count()).isZero();
        }
    }

    // ===== PUT /sellers/{sellerId}/refund-policies/{policyId} - 환불정책 수정 =====

    @Nested
    @DisplayName("PUT /sellers/{sellerId}/refund-policies/{policyId} - 환불정책 수정")
    class UpdateRefundPolicyTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] 존재하는 정책 수정 성공")
        void updateRefundPolicy_ExistingPolicy_Returns204() {
            // given
            RefundPolicyJpaEntity policy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    SELLER_ID, "기존 정책"));
            Map<String, Object> request = createUpdateRequest(false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", SELLER_ID, policy.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            RefundPolicyJpaEntity updated =
                    refundPolicyRepository.findById(policy.getId()).orElseThrow();
            assertThat(updated.getPolicyName()).isEqualTo("수정된 정책");
            assertThat(updated.getReturnPeriodDays()).isEqualTo(14);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] 기본 정책으로 변경 시 기존 기본 정책 해제")
        void updateRefundPolicy_MarkAsDefault_UnmarksOldDefault() {
            // given
            RefundPolicyJpaEntity oldDefault =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            RefundPolicyJpaEntity policy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    SELLER_ID, "일반 정책"));

            Map<String, Object> request = createUpdateRequest(true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", SELLER_ID, policy.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            RefundPolicyJpaEntity newDefault =
                    refundPolicyRepository.findById(policy.getId()).orElseThrow();
            assertThat(newDefault.isDefaultPolicy()).isTrue();

            RefundPolicyJpaEntity oldPolicy =
                    refundPolicyRepository.findById(oldDefault.getId()).orElseThrow();
            assertThat(oldPolicy.isDefaultPolicy()).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-3] 존재하지 않는 정책 수정 시 404")
        void updateRefundPolicy_NonExistingPolicy_Returns404() {
            // given
            Long nonExistingId = 99999L;
            Map<String, Object> request = createUpdateRequest(false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", SELLER_ID, nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-4] 다른 셀러의 정책 수정 시 404")
        void updateRefundPolicy_OtherSellerPolicy_Returns404() {
            // given
            RefundPolicyJpaEntity otherPolicy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newDefaultEntity(OTHER_SELLER_ID));
            Map<String, Object> request = createUpdateRequest(false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", SELLER_ID, otherPolicy.getId())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());

            // DB 검증: 데이터 변경 안 됨
            RefundPolicyJpaEntity unchanged =
                    refundPolicyRepository.findById(otherPolicy.getId()).orElseThrow();
            assertThat(unchanged.getPolicyName()).isEqualTo(otherPolicy.getPolicyName());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-6] returnPeriodDays 범위 벗어남 수정 시 400")
        void updateRefundPolicy_InvalidReturnPeriod_Returns400() {
            // given
            RefundPolicyJpaEntity policy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            Map<String, Object> request = createUpdateRequest(false);
            request.put("returnPeriodDays", 100);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", SELLER_ID, policy.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증: 데이터 변경 안 됨
            RefundPolicyJpaEntity unchanged =
                    refundPolicyRepository.findById(policy.getId()).orElseThrow();
            assertThat(unchanged.getReturnPeriodDays())
                    .isEqualTo(RefundPolicyJpaEntityFixtures.DEFAULT_RETURN_PERIOD_DAYS);
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-7] policyName 빈 값 수정 시 400")
        void updateRefundPolicy_EmptyPolicyName_Returns400() {
            // given
            RefundPolicyJpaEntity policy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            Map<String, Object> request = createUpdateRequest(false);
            request.put("policyName", "");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{policyId}", SELLER_ID, policy.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PATCH /sellers/{sellerId}/refund-policies/status - 환불정책 상태 변경 =====

    @Nested
    @DisplayName("PATCH /sellers/{sellerId}/refund-policies/status - 환불정책 상태 변경")
    class ChangeRefundPolicyStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-1] 정책 활성화 성공 (단건)")
        void changeStatus_ActivateSinglePolicy_Returns204() {
            // given
            RefundPolicyJpaEntity inactivePolicy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newInactiveEntity(SELLER_ID));
            Map<String, Object> request =
                    Map.of("policyIds", List.of(inactivePolicy.getId()), "active", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            RefundPolicyJpaEntity activated =
                    refundPolicyRepository.findById(inactivePolicy.getId()).orElseThrow();
            assertThat(activated.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-2] 정책 비활성화 성공 (비기본 정책, 다른 활성 정책 존재)")
        void changeStatus_DeactivateNonDefaultPolicy_Returns204() {
            // given
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            RefundPolicyJpaEntity activePolicy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    SELLER_ID, "일반 정책"));

            Map<String, Object> request =
                    Map.of("policyIds", List.of(activePolicy.getId()), "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            RefundPolicyJpaEntity deactivated =
                    refundPolicyRepository.findById(activePolicy.getId()).orElseThrow();
            assertThat(deactivated.isActive()).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-3] 정책 다건 활성화 성공")
        void changeStatus_ActivateMultiplePolicies_Returns204() {
            // given
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            RefundPolicyJpaEntity inactive1 =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newInactiveEntity(SELLER_ID));
            RefundPolicyJpaEntity inactive2 =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newInactiveEntity(SELLER_ID));
            RefundPolicyJpaEntity inactive3 =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newInactiveEntity(SELLER_ID));

            Map<String, Object> request =
                    Map.of(
                            "policyIds",
                            List.of(inactive1.getId(), inactive2.getId(), inactive3.getId()),
                            "active",
                            true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            assertThat(refundPolicyRepository.findById(inactive1.getId()).orElseThrow().isActive())
                    .isTrue();
            assertThat(refundPolicyRepository.findById(inactive2.getId()).orElseThrow().isActive())
                    .isTrue();
            assertThat(refundPolicyRepository.findById(inactive3.getId()).orElseThrow().isActive())
                    .isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-4] 존재하지 않는 정책 ID 포함 시 404")
        void changeStatus_NonExistingPolicy_Returns404() {
            // given
            RefundPolicyJpaEntity policy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));

            Map<String, Object> request =
                    Map.of("policyIds", List.of(policy.getId(), 99999L), "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());

            // DB 검증: 트랜잭션 롤백으로 상태 변경 안 됨
            RefundPolicyJpaEntity unchanged =
                    refundPolicyRepository.findById(policy.getId()).orElseThrow();
            assertThat(unchanged.isActive()).isTrue();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-5] 기본 정책 비활성화 시도 시 400 (POL-DEACT-001)")
        void changeStatus_DeactivateDefaultPolicy_Returns400() {
            // given
            RefundPolicyJpaEntity defaultPolicy =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));

            Map<String, Object> request =
                    Map.of("policyIds", List.of(defaultPolicy.getId()), "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증
            RefundPolicyJpaEntity unchanged =
                    refundPolicyRepository.findById(defaultPolicy.getId()).orElseThrow();
            assertThat(unchanged.isActive()).isTrue();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-6] 마지막 활성 정책 비활성화 시도 시 400 (POL-DEACT-002)")
        void changeStatus_DeactivateLastActivePolicy_Returns400() {
            // given
            RefundPolicyJpaEntity lastActive =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    SELLER_ID, "마지막 활성 정책"));
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newInactiveEntity(SELLER_ID));
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newInactiveEntity(SELLER_ID));

            Map<String, Object> request =
                    Map.of("policyIds", List.of(lastActive.getId()), "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/status", SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증
            RefundPolicyJpaEntity unchanged =
                    refundPolicyRepository.findById(lastActive.getId()).orElseThrow();
            assertThat(unchanged.isActive()).isTrue();
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createRegisterRequest(boolean defaultPolicy) {
        Map<String, Object> request = new HashMap<>();
        request.put("policyName", "테스트 환불정책");
        request.put("defaultPolicy", defaultPolicy);
        request.put("returnPeriodDays", 7);
        request.put("exchangePeriodDays", 14);
        request.put("nonReturnableConditions", List.of("OPENED_PACKAGING", "USED_PRODUCT"));
        request.put("partialRefundEnabled", true);
        request.put("inspectionRequired", true);
        request.put("inspectionPeriodDays", 3);
        request.put("additionalInfo", "추가 안내 문구");
        return request;
    }

    private Map<String, Object> createUpdateRequest(boolean defaultPolicy) {
        Map<String, Object> request = new HashMap<>();
        request.put("policyName", "수정된 정책");
        request.put("defaultPolicy", defaultPolicy);
        request.put("returnPeriodDays", 14);
        request.put("exchangePeriodDays", 30);
        request.put("nonReturnableConditions", List.of("TIME_EXPIRED"));
        request.put("partialRefundEnabled", false);
        request.put("inspectionRequired", false);
        request.put("inspectionPeriodDays", 0);
        request.put("additionalInfo", "수정된 추가 안내");
        return request;
    }
}
