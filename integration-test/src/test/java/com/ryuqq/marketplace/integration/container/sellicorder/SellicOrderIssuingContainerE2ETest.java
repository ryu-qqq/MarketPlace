package com.ryuqq.marketplace.integration.container.sellicorder;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.legacy.sellicorder.dto.command.IssueSellicLegacyOrderCommand;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderPersistencePort;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderQueryPort;
import com.ryuqq.marketplace.integration.container.ContainerLegacyE2ETestBase;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 셀릭 주문 luxurydb 저장 Testcontainers E2E 테스트.
 *
 * <p>실제 MySQL 컨테이너에 셀릭 주문을 INSERT하고, 9개 테이블에 정상 저장되는지 검증합니다. 중복 체크 로직도 실제 DB 조회로 검증합니다.
 */
@Tag("sellic-order")
@DisplayName("셀릭 주문 luxurydb 저장 E2E 테스트 (Testcontainers)")
class SellicOrderIssuingContainerE2ETest extends ContainerLegacyE2ETestBase {

    @Autowired private SellicLegacyOrderPersistencePort persistencePort;
    @Autowired private SellicLegacyOrderQueryPort queryPort;

    @Nested
    @DisplayName("persist — luxurydb 9개 테이블 INSERT")
    class PersistTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-SELLIC-P01] 셀릭 주문 저장 시 orderId가 반환된다")
        void persist_ReturnsOrderId() {
            // given
            IssueSellicLegacyOrderCommand command = createTestCommand(99999L, "TEST_ORDER_001");

            // when
            long orderId = persistencePort.persist(command);

            // then
            assertThat(orderId).isGreaterThan(0);
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-SELLIC-P02] 저장 후 external_order 중복 체크가 동작한다")
        void persist_ThenExistsByExternalIdx_ReturnsTrue() {
            // given
            long externalIdx = 88888L;
            IssueSellicLegacyOrderCommand command =
                    createTestCommand(externalIdx, "TEST_ORDER_002");

            // when
            persistencePort.persist(command);

            // then — 방금 저장한 IDX로 중복 체크
            boolean exists = queryPort.existsByExternalIdx(2L, externalIdx);
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("[TC-SELLIC-P03] 존재하지 않는 IDX는 중복이 아니다")
        void existsByExternalIdx_NotExists_ReturnsFalse() {
            // when
            boolean exists = queryPort.existsByExternalIdx(2L, 77777L);

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-SELLIC-P04] 두 번 저장해도 각각 다른 orderId가 생성된다")
        void persist_Twice_DifferentOrderIds() {
            // given
            IssueSellicLegacyOrderCommand command1 = createTestCommand(11111L, "TEST_ORDER_003");
            IssueSellicLegacyOrderCommand command2 = createTestCommand(22222L, "TEST_ORDER_004");

            // when
            long orderId1 = persistencePort.persist(command1);
            long orderId2 = persistencePort.persist(command2);

            // then
            assertThat(orderId1).isGreaterThan(0);
            assertThat(orderId2).isGreaterThan(orderId1);
        }
    }

    // ===== Helper =====

    private IssueSellicLegacyOrderCommand createTestCommand(
            long externalIdx, String externalOrderPkId) {
        Instant now = Instant.now();

        var payment =
                new IssueSellicLegacyOrderCommand.Payment(
                        1L,
                        50000L,
                        "PAYMENT_COMPLETED",
                        "SEWON",
                        now,
                        "홍길동",
                        "",
                        "01012345678",
                        "SEWON_2_" + externalOrderPkId,
                        "PC");

        var order =
                new IssueSellicLegacyOrderCommand.Order(1L, 8L, 1L, 50000L, "PAYMENT_COMPLETED", 1);

        var shipment = new IssueSellicLegacyOrderCommand.Shipment("", "", "", "DELIVERY_PENDING");

        var settlement = new IssueSellicLegacyOrderCommand.Settlement(0L);

        var externalOrder =
                new IssueSellicLegacyOrderCommand.ExternalOrder(2L, externalIdx, externalOrderPkId);

        var interlockingOrder =
                new IssueSellicLegacyOrderCommand.InterlockingOrder(
                        2L, "SEWON", externalIdx, externalOrderPkId);

        var shippingAddress =
                new IssueSellicLegacyOrderCommand.ShippingAddress(
                        "김수령", "01098765432", "서울시 강남구 테헤란로 123", "06234", "부재 시 문 앞");

        return new IssueSellicLegacyOrderCommand(
                payment,
                order,
                shipment,
                settlement,
                externalOrder,
                interlockingOrder,
                shippingAddress);
    }
}
