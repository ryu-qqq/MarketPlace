package com.ryuqq.marketplace.integration.container.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.integration.container.ContainerE2ETestBase;
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
 * Exchange Command Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 교환 명령 API를 검증합니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C1~C3: POST /exchanges/request/batch - 교환 요청 일괄
 *   <li>C4~C5: POST /exchanges/approve/batch - 교환 승인 일괄
 *   <li>C6: POST /exchanges/collect/batch - 교환 수거 완료 일괄
 *   <li>C7: POST /exchanges/prepare/batch - 교환 준비 완료 일괄
 *   <li>C8~C9: POST /exchanges/reject/batch - 교환 거절 일괄
 *   <li>C10~C11: PATCH /exchanges/hold/batch - 교환 보류/해제 일괄
 *   <li>C12: POST /exchanges/convert-to-refund/batch - 교환 건 환불 전환
 *   <li>C13~C14: POST /exchanges/{exchangeClaimId}/histories - 수기 메모 등록
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("exchange")
@Tag("command")
@DisplayName("Exchange Command Container E2E 테스트")
@SuppressWarnings("PMD.TooManyMethods")
class ExchangeContainerCommandE2ETest extends ContainerE2ETestBase {

    private static final String REQUEST_BATCH = "/exchanges/request/batch";
    private static final String APPROVE_BATCH = "/exchanges/approve/batch";
    private static final String COLLECT_BATCH = "/exchanges/collect/batch";
    private static final String PREPARE_BATCH = "/exchanges/prepare/batch";
    private static final String REJECT_BATCH = "/exchanges/reject/batch";
    private static final String HOLD_BATCH = "/exchanges/hold/batch";
    private static final String CONVERT_TO_REFUND_BATCH = "/exchanges/convert-to-refund/batch";
    private static final String HISTORIES = "/exchanges/{exchangeClaimId}/histories";

    @Autowired private ExchangeClaimJpaRepository exchangeClaimRepository;
    @Autowired private ExchangeOutboxJpaRepository exchangeOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private RefundClaimJpaRepository refundClaimRepository;
    @Autowired private RefundOutboxJpaRepository refundOutboxRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        exchangeOutboxRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        exchangeOutboxRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== POST /exchanges/request/batch =====

    @Nested
    @DisplayName("POST /exchanges/request/batch - 교환 요청 일괄")
    class RequestBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1] 정상 교환 요청 -> 성공 + ExchangeClaim 생성")
        void requestBatch_success() {
            // given
            String orderId = "order-exchange-req-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 30000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // when
            givenSuperAdmin()
                    .body(Map.of("items", List.of(createExchangeRequestItem(orderItemId))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // then
            assertThat(exchangeClaimRepository.count()).isEqualTo(1);
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2] 빈 items 요청 시 400 에러")
        void requestBatch_emptyItems_badRequest() {
            givenSuperAdmin()
                    .body(Map.of("items", List.of()))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3] 권한 없는 사용자가 교환 요청 시 403")
        void requestBatch_noPermission_forbidden() {
            givenAuthenticatedUser()
                    .body(Map.of("items", List.of(createExchangeRequestItem("dummy-id"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== POST /exchanges/approve/batch =====

    @Nested
    @DisplayName("POST /exchanges/approve/batch - 교환 승인 일괄")
    class ApproveBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C4] REQUESTED 상태 교환 승인 -> 성공")
        void approveBatch_success() {
            // given
            String exchangeClaimId = seedExchangeRequest("order-exchange-approve-001");

            // when
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C5] 존재하지 않는 exchangeClaimId로 승인 시 403 에러")
        void approveBatch_nonExistentId_forbidden() {
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of("non-existent-id")))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== POST /exchanges/collect/batch =====

    @Nested
    @DisplayName("POST /exchanges/collect/batch - 교환 수거 완료 일괄")
    class CollectBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C6] 승인(COLLECTING) 상태 교환 수거 완료 -> 성공")
        void collectBatch_success() {
            // given: 교환 요청 + 승인
            String exchangeClaimId = seedExchangeRequest("order-exchange-collect-001");

            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // when
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(COLLECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }
    }

    // ===== POST /exchanges/prepare/batch =====

    @Nested
    @DisplayName("POST /exchanges/prepare/batch - 교환 준비 완료 일괄")
    class PrepareBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C7] 수거완료(COLLECTED) 상태 교환 준비 완료 -> 성공")
        void prepareBatch_success() {
            // given: 요청 + 승인 + 수거완료
            String exchangeClaimId = seedExchangeRequest("order-exchange-prepare-001");

            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(COLLECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // when
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(PREPARE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }
    }

    // ===== POST /exchanges/reject/batch =====

    @Nested
    @DisplayName("POST /exchanges/reject/batch - 교환 거절 일괄")
    class RejectBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C8] REQUESTED 상태 교환 거절 -> 성공")
        void rejectBatch_success() {
            // given
            String exchangeClaimId = seedExchangeRequest("order-exchange-reject-001");

            // when
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C9] 빈 exchangeClaimIds 요청 시 400 에러")
        void rejectBatch_emptyIds_badRequest() {
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of()))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PATCH /exchanges/hold/batch =====

    @Nested
    @DisplayName("PATCH /exchanges/hold/batch - 교환 보류/해제 일괄")
    class HoldBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C10] 교환 보류 설정 -> 성공")
        void holdBatch_hold_success() {
            // given
            String exchangeClaimId = seedExchangeRequest("order-exchange-hold-001");

            // when
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "exchangeClaimIds",
                                    List.of(exchangeClaimId),
                                    "isHold",
                                    true,
                                    "memo",
                                    "검수 대기"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C11] 보류 해제 -> 성공")
        void holdBatch_unhold_success() {
            // given
            String exchangeClaimId = seedExchangeRequest("order-exchange-unhold-001");

            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "exchangeClaimIds",
                                    List.of(exchangeClaimId),
                                    "isHold",
                                    true,
                                    "memo",
                                    "검수 대기"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // when
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "exchangeClaimIds",
                                    List.of(exchangeClaimId),
                                    "isHold",
                                    false,
                                    "memo",
                                    "검수 완료"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }
    }

    // ===== POST /exchanges/convert-to-refund/batch =====

    @Nested
    @DisplayName("POST /exchanges/convert-to-refund/batch - 교환 건 환불 전환")
    class ConvertToRefundBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C12] REQUESTED 상태 교환 건 환불 전환 -> 성공")
        void convertToRefund_success() {
            // given
            String exchangeClaimId = seedExchangeRequest("order-exchange-convert-001");

            // when
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(CONVERT_TO_REFUND_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }
    }

    // ===== POST /exchanges/{exchangeClaimId}/histories =====

    @Nested
    @DisplayName("POST /exchanges/{exchangeClaimId}/histories - 수기 메모 등록")
    class AddMemoTest {

        @Test
        @Tag("P0")
        @DisplayName("[C13] 교환 건에 수기 메모 등록 -> 201")
        void addMemo_success() {
            // given
            String exchangeClaimId = seedExchangeRequest("order-exchange-memo-001");

            // when
            givenSuperAdmin()
                    .body(Map.of("message", "고객과 통화 완료, 수거 예정"))
                    .when()
                    .post(HISTORIES, exchangeClaimId)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.historyId", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C14] 빈 메모 등록 시 400 에러")
        void addMemo_emptyMessage_badRequest() {
            givenSuperAdmin()
                    .body(Map.of("message", ""))
                    .when()
                    .post(HISTORIES, "any-exchange-id")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== Helper =====

    /** 교환 요청을 시딩하고 생성된 exchangeClaimId를 반환한다. */
    private String seedExchangeRequest(String orderId) {
        orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
        OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 30000, 1);
        String orderItemId = orderItemRepository.save(item).getId();

        givenSuperAdmin()
                .body(Map.of("items", List.of(createExchangeRequestItem(orderItemId))))
                .when()
                .post(REQUEST_BATCH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.successCount", equalTo(1));

        return exchangeClaimRepository.findAll().stream()
                .filter(e -> "REQUESTED".equals(e.getExchangeStatus()))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    private Map<String, Object> createExchangeRequestItem(String orderItemId) {
        return Map.ofEntries(
                Map.entry("orderId", orderItemId),
                Map.entry("exchangeQty", 1),
                Map.entry("reasonType", "SIZE_CHANGE"),
                Map.entry("reasonDetail", "사이즈 변경 원합니다"),
                Map.entry("originalProductId", 1000L),
                Map.entry("originalSkuCode", "SKU-RED-M"),
                Map.entry("targetProductGroupId", 1001L),
                Map.entry("targetProductId", 2001L),
                Map.entry("targetSkuCode", "SKU-RED-XL"),
                Map.entry("targetQuantity", 1));
    }
}
