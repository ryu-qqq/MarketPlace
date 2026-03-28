package com.ryuqq.marketplace.application.legacyconversion.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderStatusSyncBundle;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderCompositeReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyOrderStatusSyncCoordinator 단위 테스트.
 *
 * <p>상태 비교 후 동기화 위임/건너뜀 분기를 검증합니다.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyOrderStatusSyncCoordinator 단위 테스트")
class LegacyOrderStatusSyncCoordinatorTest {

    @Mock private LegacyOrderCompositeReadManager compositeReadManager;
    @Mock private LegacyOrderStatusMapper statusMapper;
    @Mock private OrderReadManager orderReadManager;
    @Mock private LegacyOrderStatusUpdateFacade updateFacade;
    @Mock private LegacyOrderConversionOutboxCommandManager outboxCommandManager;

    @InjectMocks private LegacyOrderStatusSyncCoordinator coordinator;

    @Captor private ArgumentCaptor<LegacyOrderStatusSyncBundle> syncBundleCaptor;

    private static final long LEGACY_ORDER_ID = 12345L;
    private static final long LEGACY_PAYMENT_ID = 67890L;
    private static final String INTERNAL_ORDER_ID = "01900000-0000-7000-8000-000000000001";
    private static final Instant NOW = Instant.parse("2026-03-27T10:00:00Z");

    @Nested
    @DisplayName("상태 동일 시 건너뜀")
    class StatusSameTest {

        @Test
        @DisplayName("레거시 상태와 market 상태가 동일하면 UpdateFacade를 호출하지 않습니다")
        void sync_WithSameStatus_SkipsUpdate() {
            // given
            LegacyOrderIdMapping mapping = createMapping();
            LegacyOrderConversionOutbox outbox = createOutbox();

            // 레거시 상태: ORDER_PROCESSING → READY
            LegacyOrderCompositeResult composite = createComposite("ORDER_PROCESSING");
            given(compositeReadManager.fetchOrderComposite(LEGACY_ORDER_ID)).willReturn(composite);
            given(statusMapper.isEligibleForMigration("ORDER_PROCESSING")).willReturn(true);
            given(statusMapper.resolve("ORDER_PROCESSING"))
                    .willReturn(LegacyOrderStatusMapper.OrderStatusResolution.normalOrder(null));

            // market DB: OrderItem READY (reconstitutedOrder default)
            Order order = OrderFixtures.reconstitutedOrder();
            given(orderReadManager.getById(OrderId.of(INTERNAL_ORDER_ID))).willReturn(order);

            // when
            coordinator.sync(mapping, outbox);

            // then
            then(updateFacade).shouldHaveNoInteractions();
            then(outboxCommandManager).should().persist(outbox);
        }
    }

    @Nested
    @DisplayName("상태 변경 시 UpdateFacade 위임")
    class StatusChangedTest {

        @Test
        @DisplayName("레거시 발주확인이면 READY→CONFIRMED 전환을 위해 UpdateFacade에 위임합니다")
        void sync_WithDeliveryPending_DelegatesToUpdateFacade() {
            // given
            LegacyOrderIdMapping mapping = createMapping();
            LegacyOrderConversionOutbox outbox = createOutbox();

            // 레거시 상태: DELIVERY_PENDING → CONFIRMED
            LegacyOrderCompositeResult composite = createComposite("DELIVERY_PENDING");
            given(compositeReadManager.fetchOrderComposite(LEGACY_ORDER_ID)).willReturn(composite);
            given(statusMapper.isEligibleForMigration("DELIVERY_PENDING")).willReturn(true);
            given(statusMapper.resolve("DELIVERY_PENDING"))
                    .willReturn(
                            LegacyOrderStatusMapper.OrderStatusResolution.normalOrder(
                                    ShipmentStatus.READY));

            // market DB: OrderItem READY
            Order order = OrderFixtures.reconstitutedOrder();
            given(orderReadManager.getById(OrderId.of(INTERNAL_ORDER_ID))).willReturn(order);

            // when
            coordinator.sync(mapping, outbox);

            // then
            then(updateFacade)
                    .should()
                    .syncStatus(syncBundleCaptor.capture(), eq(composite), any(Instant.class));

            LegacyOrderStatusSyncBundle captured = syncBundleCaptor.getValue();
            org.assertj.core.api.Assertions.assertThat(captured.currentStatus())
                    .isEqualTo(OrderItemStatus.READY);
            org.assertj.core.api.Assertions.assertThat(captured.legacyOrderId())
                    .isEqualTo(LEGACY_ORDER_ID);
        }
    }

    @Nested
    @DisplayName("이관 제외 대상")
    class IneligibleTest {

        @Test
        @DisplayName("ORDER_FAILED 상태이면 동기화를 건너뜁니다")
        void sync_WithOrderFailed_Skips() {
            // given
            LegacyOrderIdMapping mapping = createMapping();
            LegacyOrderConversionOutbox outbox = createOutbox();

            LegacyOrderCompositeResult composite = createComposite("ORDER_FAILED");
            given(compositeReadManager.fetchOrderComposite(LEGACY_ORDER_ID)).willReturn(composite);
            given(statusMapper.isEligibleForMigration("ORDER_FAILED")).willReturn(false);

            // when
            coordinator.sync(mapping, outbox);

            // then
            then(updateFacade).shouldHaveNoInteractions();
            then(orderReadManager).shouldHaveNoInteractions();
            then(outboxCommandManager).should().persist(outbox);
        }
    }

    @Nested
    @DisplayName("에러 발생 시 실패 처리")
    class ErrorHandlingTest {

        @Test
        @DisplayName("예외 발생 시 failInNewTransaction으로 실패 처리합니다")
        void sync_WithException_CallsFailInNewTransaction() {
            // given
            LegacyOrderIdMapping mapping = createMapping();
            LegacyOrderConversionOutbox outbox = createOutbox();

            given(compositeReadManager.fetchOrderComposite(LEGACY_ORDER_ID))
                    .willThrow(new RuntimeException("DB 연결 실패"));

            // when
            coordinator.sync(mapping, outbox);

            // then
            then(outboxCommandManager)
                    .should()
                    .failInNewTransaction(eq(outbox), any(String.class), any(Instant.class));
        }
    }

    private LegacyOrderIdMapping createMapping() {
        return LegacyOrderIdMapping.forNew(
                LEGACY_ORDER_ID, LEGACY_PAYMENT_ID, INTERNAL_ORDER_ID, 1001L, 1L, "SETOF", NOW);
    }

    private LegacyOrderConversionOutbox createOutbox() {
        return LegacyOrderConversionOutbox.forNew(LEGACY_ORDER_ID, LEGACY_PAYMENT_ID, NOW);
    }

    private LegacyOrderCompositeResult createComposite(String orderStatus) {
        return new LegacyOrderCompositeResult(
                LEGACY_ORDER_ID,
                LEGACY_PAYMENT_ID,
                1000L,
                1L,
                999L,
                20000L,
                orderStatus,
                1,
                NOW,
                100L,
                "테스트 상품",
                1L,
                "브랜드",
                1L,
                30000L,
                20000L,
                0L,
                0L,
                List.of(),
                null,
                null,
                null,
                null,
                "홍길동",
                "010-1234-5678",
                "12345",
                "서울시 강남구",
                null,
                null,
                null,
                null,
                null,
                List.of());
    }
}
