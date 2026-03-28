package com.ryuqq.marketplace.application.legacyconversion.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.factory.LegacyOrderConversionFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderCompositeReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacySellerIdMappingReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyOrderConversionCoordinator 상태 동기화 분기 테스트.
 *
 * <p>이미 이관된 주문(매핑 존재)일 때 SyncCoordinator로 위임하는 분기를 검증합니다.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyOrderConversionCoordinator 상태 동기화 분기 테스트")
class LegacyOrderConversionCoordinatorSyncTest {

    @Mock private LegacyOrderCompositeReadManager compositeReadManager;
    @Mock private LegacyOrderChannelResolver channelResolver;
    @Mock private LegacyOrderStatusMapper statusMapper;
    @Mock private LegacyOrderConversionFactory conversionFactory;
    @Mock private LegacyOrderPersistenceFacade persistenceFacade;
    @Mock private LegacyOrderConversionOutboxCommandManager outboxCommandManager;
    @Mock private LegacyOrderIdMappingReadManager mappingReadManager;
    @Mock private LegacyProductIdMappingReadManager productIdMappingReadManager;
    @Mock private LegacySellerIdMappingReadManager sellerIdMappingReadManager;
    @Mock private LegacyOrderStatusSyncCoordinator statusSyncCoordinator;

    @InjectMocks private LegacyOrderConversionCoordinator coordinator;

    private static final long LEGACY_ORDER_ID = 12345L;
    private static final long LEGACY_PAYMENT_ID = 67890L;
    private static final Instant NOW = Instant.parse("2026-03-27T10:00:00Z");

    @Nested
    @DisplayName("이미 이관된 주문 분기 테스트")
    class AlreadyMigratedTest {

        @Test
        @DisplayName("매핑이 존재하면 SyncCoordinator에 위임합니다")
        void convert_WithExistingMapping_DelegatesToSyncCoordinator() {
            // given
            LegacyOrderConversionOutbox outbox = createOutbox();
            LegacyOrderIdMapping mapping = createMapping();

            given(mappingReadManager.findByLegacyOrderId(LEGACY_ORDER_ID))
                    .willReturn(Optional.of(mapping));

            // when
            coordinator.convert(outbox);

            // then
            then(statusSyncCoordinator).should().sync(eq(mapping), eq(outbox));
            then(compositeReadManager).shouldHaveNoInteractions();
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("매핑이 없으면 기존 변환 흐름을 실행합니다")
        void convert_WithNoMapping_ProceedsWithConversion() {
            // given
            LegacyOrderConversionOutbox outbox = createOutbox();

            given(mappingReadManager.findByLegacyOrderId(LEGACY_ORDER_ID))
                    .willReturn(Optional.empty());

            LegacyOrderCompositeResult composite = createComposite("ORDER_PROCESSING");
            given(compositeReadManager.fetchOrderComposite(LEGACY_ORDER_ID)).willReturn(composite);
            given(statusMapper.isEligibleForMigration("ORDER_PROCESSING")).willReturn(true);
            given(statusMapper.resolve("ORDER_PROCESSING"))
                    .willReturn(LegacyOrderStatusMapper.OrderStatusResolution.normalOrder(null));
            given(channelResolver.resolve(any(), any(), any()))
                    .willReturn(new LegacyOrderChannelResolver.ChannelResolution(1L, "SETOF"));
            given(channelResolver.resolveExternalOrderNo(any(), eq(LEGACY_ORDER_ID)))
                    .willReturn(String.valueOf(LEGACY_ORDER_ID));

            // when — 변환 흐름 진행 시 productIdMapping 조회가 발생
            // factory에서 NPE 발생할 수 있지만, 핵심은 syncCoordinator가 호출되지 않는 것
            try {
                coordinator.convert(outbox);
            } catch (Exception ignored) {
                // factory/persistence에서 발생하는 에러는 무시 (분기 검증만)
            }

            // then
            then(statusSyncCoordinator).shouldHaveNoInteractions();
            then(compositeReadManager).should().fetchOrderComposite(LEGACY_ORDER_ID);
        }
    }

    private LegacyOrderConversionOutbox createOutbox() {
        return LegacyOrderConversionOutbox.forNew(LEGACY_ORDER_ID, LEGACY_PAYMENT_ID, NOW);
    }

    private LegacyOrderIdMapping createMapping() {
        return LegacyOrderIdMapping.forNew(
                LEGACY_ORDER_ID,
                LEGACY_PAYMENT_ID,
                "01900000-0000-7000-8000-000000000001",
                1001L,
                1L,
                "SETOF",
                NOW);
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
                java.util.List.of(),
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
                java.util.List.of());
    }
}
