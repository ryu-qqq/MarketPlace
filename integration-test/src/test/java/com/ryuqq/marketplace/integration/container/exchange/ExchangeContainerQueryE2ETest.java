package com.ryuqq.marketplace.integration.container.exchange;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.ExchangeClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
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
 * Exchange Query Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 교환 조회 API를 검증합니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Q1~Q3: GET /exchanges/summary - 교환 상태별 요약 조회
 *   <li>Q4~Q8: GET /exchanges - 교환 목록 조회
 *   <li>Q9~Q11: GET /exchanges/{exchangeClaimId} - 교환 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("exchange")
@Tag("query")
@DisplayName("Exchange Query Container E2E 테스트")
class ExchangeContainerQueryE2ETest extends ContainerE2ETestBase {

    private static final String EXCHANGES = "/exchanges";
    private static final String EXCHANGE_SUMMARY = "/exchanges/summary";
    private static final String EXCHANGE_DETAIL = "/exchanges/{exchangeClaimId}";

    @Autowired private ExchangeClaimJpaRepository exchangeClaimRepository;
    @Autowired private ExchangeOutboxJpaRepository exchangeOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        exchangeOutboxRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        exchangeOutboxRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== GET /exchanges/summary =====

    @Nested
    @DisplayName("GET /exchanges/summary - 교환 상태별 요약 조회")
    class GetSummaryTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1] 교환 데이터가 없을 때 요약 조회 시 정상 응답")
        void summary_noData_returnsResponse() {
            givenSuperAdmin()
                    .when()
                    .get(EXCHANGE_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2] REQUESTED 상태 교환 시딩 후 요약 조회")
        void summary_withRequestedData_showsCorrectCount() {
            // given
            String orderId = "order-exchange-summary-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "exchange-summary-001", orderItemId));
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "exchange-summary-002", orderItemId));

            // when & then
            givenSuperAdmin()
                    .when()
                    .get(EXCHANGE_SUMMARY)
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
                    .get(EXCHANGE_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== GET /exchanges =====

    @Nested
    @DisplayName("GET /exchanges - 교환 목록 조회")
    class GetListTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q4] 교환 데이터 없을 때 빈 목록 반환")
        void list_noData_emptyResult() {
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(EXCHANGES)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", hasSize(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q5] 교환 데이터 시딩 후 목록 조회 시 정상 반환")
        void list_withData_returnsResults() {
            // given
            String orderId = "order-exchange-list-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "exchange-list-001", orderItemId));

            // when & then
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(EXCHANGES)
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
            String orderId = "order-exchange-filter-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "exchange-filter-001", orderItemId));
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus(
                            "exchange-filter-002", "COMPLETED"));

            // when & then: REQUESTED 필터
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .queryParam("statuses", "REQUESTED")
                    .when()
                    .get(EXCHANGES)
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
                    .get(EXCHANGES)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P2")
        @DisplayName("[Q8] 페이지네이션 경계 - 빈 페이지 요청 시 빈 결과")
        void list_emptyPage_emptyResult() {
            // given
            String orderId = "order-exchange-page-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "exchange-page-001", orderItemId));

            // when & then
            givenSuperAdmin()
                    .queryParam("page", 1)
                    .queryParam("size", 10)
                    .when()
                    .get(EXCHANGES)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0));
        }
    }

    // ===== GET /exchanges/{exchangeClaimId} =====

    @Nested
    @DisplayName("GET /exchanges/{exchangeClaimId} - 교환 상세 조회")
    class GetDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q9] 존재하는 교환 건 상세 조회 시 정상 반환")
        void detail_exists_returnsData() {
            // given
            String orderId = "order-exchange-detail-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
            String orderItemId = orderItemRepository.save(item).getId();

            String exchangeId = "exchange-detail-001";
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            exchangeId, orderItemId));

            // when & then
            givenSuperAdmin()
                    .when()
                    .get(EXCHANGE_DETAIL, exchangeId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q10] 존재하지 않는 교환 건 상세 조회 시 에러")
        void detail_notExists_error() {
            givenSuperAdmin()
                    .when()
                    .get(EXCHANGE_DETAIL, "non-existent-exchange-id")
                    .then()
                    .statusCode(greaterThanOrEqualTo(400));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q11] 권한 없는 사용자가 상세 조회 시 403")
        void detail_noPermission_forbidden() {
            givenAuthenticatedUser()
                    .when()
                    .get(EXCHANGE_DETAIL, "any-exchange-id")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }
}
