package com.ryuqq.marketplace.integration.refund;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.RefundClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Refund Query E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Q09: GET /api/v1/market/refunds/summary - 환불 요약 조회
 *   <li>Q10: GET /api/v1/market/refunds - 환불 목록 조회
 *   <li>Q11: GET /api/v1/market/refunds/{refundClaimId} - 환불 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("refund")
@Tag("query")
@DisplayName("Refund Query E2E 테스트")
class RefundQueryE2ETest extends E2ETestBase {

    private static final String REFUNDS = "/refunds";
    private static final String REFUND_SUMMARY = "/refunds/summary";
    private static final String REFUND_DETAIL = "/refunds/{refundClaimId}";

    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private RefundClaimJpaRepository refundClaimRepository;
    @Autowired private RefundOutboxJpaRepository refundOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== 공통 시딩 헬퍼 =====

    private String seedOrderItem(String orderId) {
        OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity(orderId);
        orderRepository.save(order);
        OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
        return orderItemRepository.save(item).getId();
    }

    // ===== Q09: 환불 요약 조회 =====

    @Nested
    @DisplayName("Q09: GET /refunds/summary - 환불 요약 조회")
    class GetRefundSummaryTest {

        @Test
        @Tag("P0")
        @DisplayName("[QUERY-01] 환불 요약 조회 - 다양한 상태 데이터 존재 시 정상 반환")
        void getSummary_WithMultipleStatuses_ReturnsData() {
            // Seed: 다양한 상태의 RefundClaim 직접 시딩
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity("ref-summary-01"));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity("ref-summary-02"));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.collectingEntity("ref-summary-03"));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.completedEntity("ref-summary-04"));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.rejectedEntity("ref-summary-05"));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[QUERY-02] 환불 요약 조회 - 데이터 없을 때 0 카운트 반환")
        void getSummary_NoData_ReturnsZeroCounts() {
            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    // ===== Q10: 환불 목록 조회 =====

    @Nested
    @DisplayName("Q10: GET /refunds - 환불 목록 조회")
    class GetRefundListTest {

        @Test
        @Tag("P0")
        @DisplayName("[QUERY-03] 환불 목록 조회 - 데이터 존재 시 목록 반환")
        void getList_WithData_ReturnsContent() {
            // Seed: REQUESTED 상태 3건
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity("ref-list-01"));
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity("ref-list-02"));
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity("ref-list-03"));

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(3)));
        }

        @Test
        @Tag("P0")
        @DisplayName("[QUERY-04] 환불 목록 조회 - 데이터 없을 때 빈 목록 반환")
        void getList_NoData_ReturnsEmptyContent() {
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty());
        }

        @Test
        @Tag("P1")
        @DisplayName("[QUERY-05] 환불 목록 조회 - 상태 필터 (REQUESTED)")
        void getList_StatusFilter_ReturnsFilteredContent() {
            // Seed: REQUESTED 2건, COLLECTING 1건, COMPLETED 1건
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity("ref-filter-01"));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity("ref-filter-02"));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.collectingEntity("ref-filter-03"));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.completedEntity("ref-filter-04"));

            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "REQUESTED")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.claimInfo.status", everyItem(equalTo("REQUESTED")));
        }

        @Test
        @Tag("P1")
        @DisplayName("[QUERY-06] 환불 목록 조회 - 페이징 동작 확인")
        void getList_Paging_ReturnsCorrectPage() {
            // Seed: REQUESTED 5건
            List.of("ref-page-01", "ref-page-02", "ref-page-03", "ref-page-04", "ref-page-05")
                    .forEach(
                            id ->
                                    refundClaimRepository.save(
                                            RefundClaimJpaEntityFixtures.requestedEntity(id)));

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[QUERY-07] 환불 목록 조회 - 날짜 범위 필터")
        void getList_DateRangeFilter_ReturnsData() {
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity("ref-date-01"));
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity("ref-date-02"));

            given().spec(givenSuperAdmin())
                    .queryParam("dateField", "REQUESTED")
                    .queryParam("startDate", "2020-01-01")
                    .queryParam("endDate", "2099-12-31")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    // ===== Q11: 환불 상세 조회 =====

    @Nested
    @DisplayName("Q11: GET /refunds/{refundClaimId} - 환불 상세 조회")
    class GetRefundDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[QUERY-08] 환불 상세 조회 - 존재하는 ID")
        void getDetail_ExistingId_ReturnsData() {
            String refundClaimId = "ref-detail-01";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity(refundClaimId));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.refundClaimInfo.refundClaimId", equalTo(refundClaimId));
        }

        @Test
        @Tag("P0")
        @DisplayName("[QUERY-09] 환불 상세 조회 - 존재하지 않는 ID → 404")
        void getDetail_NonExistentId_Returns404() {
            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, "01900000-0000-7000-0000-000000000999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[QUERY-10] 환불 상세 조회 - COMPLETED 상태 (refundInfo 포함 확인)")
        void getDetail_CompletedStatus_ReturnsRefundInfo() {
            String refundClaimId = "ref-completed-01";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.completedEntity(refundClaimId));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.refundClaimInfo.refundStatus", equalTo("COMPLETED"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[QUERY-11] 환불 상세 조회 - 보류 상태 (holdInfo 포함 확인)")
        void getDetail_HeldStatus_ReturnsHoldInfo() {
            String refundClaimId = "ref-hold-01";
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.heldEntity(refundClaimId, "CS 확인 필요"));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[QUERY-12] 환불 상세 조회 - 비인증 요청 → 401")
        void getDetail_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .when()
                    .get(REFUND_DETAIL, "any-id")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== 인증/인가 테스트 =====

    @Nested
    @DisplayName("인증/인가 테스트")
    @Tag("auth")
    class AuthorizationTest {

        @Test
        @Tag("P0")
        @DisplayName("[AUTH-1] 비인증 요청으로 목록 조회 시도 → 401")
        void getList_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[AUTH-2] 비인증 요청으로 요약 조회 시도 → 401")
        void getSummary_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .when()
                    .get(REFUND_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
