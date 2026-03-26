package com.ryuqq.marketplace.application.legacy.sellicorder.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.legacy.sellicorder.dto.command.IssueSellicLegacyOrderCommand;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderPersistencePort;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderQueryPort;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.outboundproduct.OutboundProductFixtures;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellicLegacyOrderCoordinator 단위 테스트")
class SellicLegacyOrderCoordinatorTest {

    private static final long SELLIC_SITE_ID = 2L;
    private static final long SALES_CHANNEL_ID = 16L;

    @InjectMocks private SellicLegacyOrderCoordinator sut;

    @Mock private SellicLegacyOrderQueryPort queryPort;
    @Mock private SellicLegacyOrderPersistencePort persistencePort;
    @Mock private OutboundProductReadManager outboundProductReadManager;
    @Mock private LegacyOrderConversionOutboxCommandManager outboxCommandManager;

    // ===== 헬퍼 메서드 =====

    private static ExternalOrderItemPayload createItemPayload(
            String externalProductOrderId, String externalProductId) {
        return new ExternalOrderItemPayload(
                externalProductOrderId,
                externalProductId,
                "OPT-001",
                "테스트 상품",
                "옵션 A",
                "https://img.example.com/1.jpg",
                50000,
                1,
                50000,
                0,
                0,
                50000,
                "홍길동",
                "010-1234-5678",
                "12345",
                "서울특별시 강남구",
                "101동 201호",
                "부재시 경비실",
                "PAYED");
    }

    private static ExternalOrderPayload createOrderPayload(
            String externalOrderNo, List<ExternalOrderItemPayload> items) {
        return new ExternalOrderPayload(
                externalOrderNo,
                Instant.parse("2026-03-25T10:00:00Z"),
                "구매자",
                "buyer@test.com",
                "010-0000-0000",
                "CARD",
                50000,
                Instant.parse("2026-03-25T10:00:00Z"),
                items);
    }

    @Nested
    @DisplayName("issueIfNotDuplicate() - 셀릭 주문 저장 + Outbox 생성")
    class IssueIfNotDuplicateTest {

        @Test
        @DisplayName("중복 아닌 주문이면 luxurydb 저장 + Outbox 생성 후 true를 반환한다")
        void issueIfNotDuplicate_NewOrder_PersistsAndReturnsTrue() {
            // given
            Instant now = Instant.now();
            ExternalOrderItemPayload item = createItemPayload("100", "OWN-001");
            ExternalOrderPayload payload = createOrderPayload("ORD-001", List.of(item));
            OutboundProduct outboundProduct = OutboundProductFixtures.registeredProduct();

            given(queryPort.existsByExternalIdx(SELLIC_SITE_ID, 100L)).willReturn(false);
            given(
                            outboundProductReadManager.findByExternalProductIdsAndSalesChannelId(
                                    Set.of("OWN-001"), SALES_CHANNEL_ID))
                    .willReturn(List.of(outboundProduct));
            given(persistencePort.persist(any(IssueSellicLegacyOrderCommand.class)))
                    .willReturn(1000L);

            // when
            boolean result = sut.issueIfNotDuplicate(payload, SALES_CHANNEL_ID, now);

            // then
            assertThat(result).isTrue();
            then(persistencePort).should().persist(any(IssueSellicLegacyOrderCommand.class));
            then(outboxCommandManager).should().persist(any(LegacyOrderConversionOutbox.class));
        }

        @Test
        @DisplayName("중복 주문이면 false를 반환하고 저장하지 않는다")
        void issueIfNotDuplicate_DuplicateOrder_ReturnsFalse() {
            // given
            Instant now = Instant.now();
            ExternalOrderItemPayload item = createItemPayload("200", "OWN-002");
            ExternalOrderPayload payload = createOrderPayload("ORD-002", List.of(item));

            given(queryPort.existsByExternalIdx(SELLIC_SITE_ID, 200L)).willReturn(true);

            // when
            boolean result = sut.issueIfNotDuplicate(payload, SALES_CHANNEL_ID, now);

            // then
            assertThat(result).isFalse();
            then(persistencePort).should(never()).persist(any());
            then(outboxCommandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("OutboundProduct 매핑이 없으면 false를 반환한다")
        void issueIfNotDuplicate_NoOutboundProduct_ReturnsFalse() {
            // given
            Instant now = Instant.now();
            ExternalOrderItemPayload item = createItemPayload("300", "OWN-UNKNOWN");
            ExternalOrderPayload payload = createOrderPayload("ORD-003", List.of(item));

            given(queryPort.existsByExternalIdx(SELLIC_SITE_ID, 300L)).willReturn(false);
            given(
                            outboundProductReadManager.findByExternalProductIdsAndSalesChannelId(
                                    Set.of("OWN-UNKNOWN"), SALES_CHANNEL_ID))
                    .willReturn(List.of());

            // when
            boolean result = sut.issueIfNotDuplicate(payload, SALES_CHANNEL_ID, now);

            // then
            assertThat(result).isFalse();
            then(persistencePort).should(never()).persist(any());
            then(outboxCommandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("아이템이 빈 목록이면 false를 반환한다")
        void issueIfNotDuplicate_EmptyItems_ReturnsFalse() {
            // given
            Instant now = Instant.now();
            ExternalOrderPayload payload = createOrderPayload("ORD-004", List.of());

            // when
            boolean result = sut.issueIfNotDuplicate(payload, SALES_CHANNEL_ID, now);

            // then
            assertThat(result).isFalse();
            then(queryPort).should(never()).existsByExternalIdx(anyLong(), anyLong());
            then(persistencePort).should(never()).persist(any());
            then(outboxCommandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("다중 아이템 주문이면 아이템별로 persist를 호출한다")
        void issueIfNotDuplicate_MultipleItems_PersistsEachItem() {
            // given
            Instant now = Instant.now();
            ExternalOrderItemPayload item1 = createItemPayload("500", "OWN-005");
            ExternalOrderItemPayload item2 = createItemPayload("501", "OWN-005");
            ExternalOrderPayload payload = createOrderPayload("ORD-005", List.of(item1, item2));
            OutboundProduct outboundProduct = OutboundProductFixtures.registeredProduct();

            // 첫 번째 아이템 중복 체크 (주문 레벨)
            given(queryPort.existsByExternalIdx(SELLIC_SITE_ID, 500L)).willReturn(false);
            // 아이템별 중복 체크
            given(queryPort.existsByExternalIdx(SELLIC_SITE_ID, 501L)).willReturn(false);
            given(
                            outboundProductReadManager.findByExternalProductIdsAndSalesChannelId(
                                    Set.of("OWN-005"), SALES_CHANNEL_ID))
                    .willReturn(List.of(outboundProduct));
            given(persistencePort.persist(any(IssueSellicLegacyOrderCommand.class)))
                    .willReturn(1001L);

            // when
            boolean result = sut.issueIfNotDuplicate(payload, SALES_CHANNEL_ID, now);

            // then
            assertThat(result).isTrue();
            then(persistencePort)
                    .should(times(2))
                    .persist(any(IssueSellicLegacyOrderCommand.class));
            then(outboxCommandManager).should().persist(any(LegacyOrderConversionOutbox.class));
        }
    }
}
