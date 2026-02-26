package com.ryuqq.marketplace.integration.refundpolicy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository.RefundPolicyJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * RefundPolicy Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /sellers/{sellerId}/refund-policies - 환불정책 목록 조회
 *
 * <p>우선순위: - P0: 4개 시나리오 (필수 기능) - P1: 3개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("refundpolicy")
@Tag("query")
@DisplayName("RefundPolicy Query API E2E 테스트")
class RefundPolicyQueryE2ETest extends E2ETestBase {

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

    // ===== GET /sellers/{sellerId}/refund-policies - 환불정책 목록 조회 =====

    @Nested
    @DisplayName("GET /sellers/{sellerId}/refund-policies - 환불정책 목록 조회")
    class SearchRefundPoliciesTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 정책 존재 시 정상 조회")
        void searchRefundPolicies_WithPolicies_Returns200() {
            // given
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "정책2"));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "정책3"));

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.totalElements", equalTo(3))
                    .body("data.content[0].policyId", notNullValue())
                    .body("data.content[0].policyName", notNullValue())
                    .body("data.content[0].defaultPolicy", notNullValue())
                    .body("data.content[0].active", notNullValue())
                    .body("data.content[0].returnPeriodDays", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 정책 없을 때 빈 목록 반환")
        void searchRefundPolicies_NoData_ReturnsEmptyList() {
            // given
            // 다른 셀러의 정책만 존재
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newDefaultEntity(OTHER_SELLER_ID));

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, 999L)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-3] 페이징 동작 확인")
        void searchRefundPolicies_Paging_Returns200() {
            // given
            for (int i = 1; i <= 5; i++) {
                refundPolicyRepository.save(
                        RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "정책" + i));
            }

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.totalElements", equalTo(5))
                    .body("data.totalPages", greaterThanOrEqualTo(3));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-4] 정렬 기능 확인 (CREATED_AT DESC)")
        void searchRefundPolicies_SortByCreatedAtDesc_Returns200() throws InterruptedException {
            // given
            var policy1 =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    SELLER_ID, "정책1"));
            Thread.sleep(50);
            var policy2 =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    SELLER_ID, "정책2"));
            Thread.sleep(50);
            var policy3 =
                    refundPolicyRepository.save(
                            RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    SELLER_ID, "정책3"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("sortDirection", "DESC")
                    .when()
                    .get(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].policyId", equalTo(policy3.getId().intValue()))
                    .body("data.content[1].policyId", equalTo(policy2.getId().intValue()))
                    .body("data.content[2].policyId", equalTo(policy1.getId().intValue()));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-5] 정렬 기능 확인 (POLICY_NAME ASC)")
        void searchRefundPolicies_SortByPolicyNameAsc_Returns200() {
            // given
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "C정책"));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "A정책"));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "B정책"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "POLICY_NAME")
                    .queryParam("sortDirection", "ASC")
                    .when()
                    .get(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].policyName", equalTo("A정책"))
                    .body("data.content[1].policyName", equalTo("B정책"))
                    .body("data.content[2].policyName", equalTo("C정책"));
        }
    }

    @Nested
    @DisplayName("셀러 격리 및 소프트 삭제 검증")
    class SellerIsolationAndSoftDeleteTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2-1] 다른 셀러 정책 조회 불가")
        void searchRefundPolicies_OnlyOwnSellerPolicies_Returns200() {
            // given
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newDefaultEntity(SELLER_ID));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "정책2"));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "정책3"));

            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newDefaultEntity(OTHER_SELLER_ID));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                            OTHER_SELLER_ID, "다른셀러정책"));

            // when & then - SELLER_ID=1 조회 시 자신의 정책만 조회됨
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-2] 삭제된 정책 조회 제외")
        void searchRefundPolicies_ExcludeDeletedPolicies_Returns200() {
            // given
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "활성정책1"));
            refundPolicyRepository.save(
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(SELLER_ID, "활성정책2"));
            refundPolicyRepository.save(RefundPolicyJpaEntityFixtures.newDeletedEntity(SELLER_ID));

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2));
        }
    }
}
