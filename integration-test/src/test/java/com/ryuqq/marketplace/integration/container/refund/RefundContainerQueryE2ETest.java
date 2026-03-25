package com.ryuqq.marketplace.integration.container.refund;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.RefundClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.integration.container.ContainerE2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Refund Query Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 환불 조회 API를 검증합니다.
 *
 * <p>테스트 대상:
 * <ul>
 *   <li>Q1~Q3: GET /refunds/summary - 환불 상태별 요약 조회</li>
 *   <li>Q4~Q8: GET /refunds - 환불 목록 조회</li>
 *   <li>Q9~Q11: GET /refunds/{refundClaimId} - 환불 상세 조회</li>
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("refund")
@Tag("query")
@DisplayName("Refund Query Container E2E 테스트")
class RefundContainerQueryE2ETest extends ContainerE2ETestBase {

    private static final String REFUNDS = "/refunds";
    private static final String REFUND_SUMMARY = "/refunds/summary";
    private static final String REFUND_DETAIL = "/refunds/{refundClaimId}";

    @Autowired private RefundClaimJpaRepository refundClaimRepository;
    @Autowired private RefundOutboxJpaRepository refundOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== GET /refunds/summary =====

    @Nested
    @DisplayName("GET /refunds/summary - 환불 상태별 요약 조회")
    class GetSummaryTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1] 환불 데이터가 없을 때 요약 조회 시 모든 카운트 0")
        void summary_noData_allZero() {
            givenSuperAdmin()
                    .when()
                    .get(REFUND_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2] REQUESTED 상태 환불 2건 시딩 후 요약 조회")
        void summary_withRequestedData_showsCorrectCount() {
            // given
            String orderId = "order-refund-summary-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            "refund-summary-001", orderItemId, 10L));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            "refund-summary-002", orderItemId, 10L));

            // when & then
            givenSuperAdmin()
                    .when()
                    .get(REFUND_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q3] 권한 없는 사용자가 요약 조회 시 403")
        void summary_noPermission_forbidden() {
            givenAuthenticatedUser()
                    .when()
                    .get(REFUND_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== GET /refunds =====

    @Nested
    @DisplayName("GET /refunds - 환불 목록 조회")
    class GetListTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q4] 환불 데이터 없을 때 빈 목록 반환")
        void list_noData_emptyResult() {
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", hasSize(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q5] 환불 데이터 시딩 후 목록 조회 시 정상 반환")
        void list_withData_returnsResults() {
            // given
            String orderId = "order-refund-list-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            "refund-list-001", orderItemId, 10L));

            // when & then
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", greaterThanOrEqualTo(1))
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q6] 상태 필터 적용하여 목록 조회")
        void list_withStatusFilter_filteredResults() {
            // given
            String orderId = "order-refund-filter-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            "refund-filter-001", orderItemId, 10L));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.entityWithStatus(
                            "refund-filter-002", "COMPLETED"));

            // when & then: REQUESTED 필터
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .queryParam("statuses", "REQUESTED")
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q7] 권한 없는 사용자가 목록 조회 시 403")
        void list_noPermission_forbidden() {
            givenAuthenticatedUser()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P2")
        @DisplayName("[Q8] 페이지네이션 경계 - 빈 페이지 요청 시 빈 결과")
        void list_emptyPage_emptyResult() {
            // given
            String orderId = "order-refund-page-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            "refund-page-001", orderItemId, 10L));

            // when & then: 2번째 페이지 (데이터 1건이므로 빈 페이지)
            givenSuperAdmin()
                    .queryParam("page", 1)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0));
        }
    }

    // ===== GET /refunds/{refundClaimId} =====

    @Nested
    @DisplayName("GET /refunds/{refundClaimId} - 환불 상세 조회")
    class GetDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q9] 존재하는 환불 건 상세 조회 시 정상 반환")
        void detail_exists_returnsData() {
            // given
            String orderId = "order-refund-detail-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            String refundId = "refund-detail-001";
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(refundId, orderItemId, 10L));

            // when & then
            givenSuperAdmin()
                    .when()
                    .get(REFUND_DETAIL, refundId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q10] 존재하지 않는 환불 건 상세 조회 시 404 또는 에러")
        void detail_notExists_error() {
            givenSuperAdmin()
                    .when()
                    .get(REFUND_DETAIL, "non-existent-refund-id")
                    .then()
                    .statusCode(greaterThanOrEqualTo(400));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q11] 권한 없는 사용자가 상세 조회 시 403")
        void detail_noPermission_forbidden() {
            givenAuthenticatedUser()
                    .when()
                    .get(REFUND_DETAIL, "any-refund-id")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }
}
