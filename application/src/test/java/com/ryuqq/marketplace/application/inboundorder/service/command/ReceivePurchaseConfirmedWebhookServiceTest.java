package com.ryuqq.marketplace.application.inboundorder.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.claimsync.ClaimSyncFixtures;
import com.ryuqq.marketplace.application.claimsync.manager.ExternalOrderItemMappingReadManager;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.ConfirmOrderUseCase;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import java.util.List;
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
@DisplayName("ReceivePurchaseConfirmedWebhookService 단위 테스트")
class ReceivePurchaseConfirmedWebhookServiceTest {

    @InjectMocks private ReceivePurchaseConfirmedWebhookService sut;

    @Mock private ExternalOrderItemMappingReadManager mappingReadManager;
    @Mock private OrderItemReadManager orderItemReadManager;
    @Mock private ConfirmOrderUseCase confirmOrderUseCase;

    private static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    private static final String DEFAULT_EXTERNAL_PRODUCT_ORDER_ID =
            ClaimSyncFixtures.DEFAULT_EXTERNAL_PRODUCT_ORDER_ID;

    @Nested
    @DisplayName("execute() - 구매 확정 웹훅 처리")
    class ExecuteTest {

        @Test
        @DisplayName("매핑된 주문상품이 READY 상태이면 구매 확정을 수행한다")
        void execute_MappedReadyOrderItem_CallsConfirmUseCase() {
            // given
            List<String> externalProductOrderIds = List.of(DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
            ExternalOrderItemMapping mapping = ClaimSyncFixtures.defaultMapping();
            OrderItemId orderItemId = mapping.orderItemId();
            OrderItem readyOrderItem = OrderFixtures.reconstitutedOrderItem();

            given(
                            mappingReadManager.getMapping(
                                    DEFAULT_SALES_CHANNEL_ID, DEFAULT_EXTERNAL_PRODUCT_ORDER_ID))
                    .willReturn(mapping);
            given(orderItemReadManager.findAllByIds(List.of(orderItemId)))
                    .willReturn(List.of(readyOrderItem));

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID, externalProductOrderIds);

            // then
            then(confirmOrderUseCase).should().execute(any(OrderItemStatusCommand.class));
        }

        @Test
        @DisplayName("매핑 정보가 없는 외부 주문상품은 확정 처리를 수행하지 않는다")
        void execute_NoMapping_SkipsConfirmation() {
            // given
            List<String> externalProductOrderIds = List.of("EXT-PO-UNKNOWN-001");

            given(mappingReadManager.getMapping(DEFAULT_SALES_CHANNEL_ID, "EXT-PO-UNKNOWN-001"))
                    .willReturn(null);

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID, externalProductOrderIds);

            // then
            then(orderItemReadManager).shouldHaveNoInteractions();
            then(confirmOrderUseCase).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("주문상품이 이미 CONFIRMED 상태이면 멱등성을 보장하고 재확정을 수행하지 않는다")
        void execute_AlreadyConfirmedOrderItem_SkipsConfirmation() {
            // given
            List<String> externalProductOrderIds = List.of(DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
            ExternalOrderItemMapping mapping = ClaimSyncFixtures.defaultMapping();
            OrderItemId orderItemId = mapping.orderItemId();
            OrderItem confirmedOrderItem = OrderFixtures.confirmedOrderItem();

            given(
                            mappingReadManager.getMapping(
                                    DEFAULT_SALES_CHANNEL_ID, DEFAULT_EXTERNAL_PRODUCT_ORDER_ID))
                    .willReturn(mapping);
            given(orderItemReadManager.findAllByIds(List.of(orderItemId)))
                    .willReturn(List.of(confirmedOrderItem));

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID, externalProductOrderIds);

            // then
            then(confirmOrderUseCase).should(never()).execute(any(OrderItemStatusCommand.class));
        }

        @Test
        @DisplayName("빈 외부 주문상품 목록을 수신하면 매핑 조회 및 확정 처리를 수행하지 않는다")
        void execute_EmptyExternalProductOrderIds_SkipsAllProcessing() {
            // given
            List<String> emptyIds = List.of();

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID, emptyIds);

            // then
            then(mappingReadManager).shouldHaveNoInteractions();
            then(orderItemReadManager).shouldHaveNoInteractions();
            then(confirmOrderUseCase).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("복수 주문상품 중 READY 상태인 것만 확정 처리된다")
        void execute_MixedStatusOrderItems_OnlyConfirmsReadyItems() {
            // given
            String extId1 = "EXT-PO-001";
            String extId2 = "EXT-PO-002";
            List<String> externalProductOrderIds = List.of(extId1, extId2);

            ExternalOrderItemMapping mapping1 = ClaimSyncFixtures.mappingWithOrderItemId(2001L);
            ExternalOrderItemMapping mapping2 = ClaimSyncFixtures.mappingWithOrderItemId(2002L);

            OrderItem readyItem = OrderFixtures.reconstitutedOrderItem();
            OrderItem confirmedItem = OrderFixtures.confirmedOrderItem();

            given(mappingReadManager.getMapping(DEFAULT_SALES_CHANNEL_ID, extId1))
                    .willReturn(mapping1);
            given(mappingReadManager.getMapping(DEFAULT_SALES_CHANNEL_ID, extId2))
                    .willReturn(mapping2);
            given(
                            orderItemReadManager.findAllByIds(
                                    List.of(mapping1.orderItemId(), mapping2.orderItemId())))
                    .willReturn(List.of(readyItem, confirmedItem));

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID, externalProductOrderIds);

            // then
            then(confirmOrderUseCase).should().execute(any(OrderItemStatusCommand.class));
        }

        @Test
        @DisplayName("모든 주문상품 조회 결과가 비어있으면 확정 처리를 수행하지 않는다")
        void execute_EmptyOrderItems_SkipsConfirmation() {
            // given
            List<String> externalProductOrderIds = List.of(DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
            ExternalOrderItemMapping mapping = ClaimSyncFixtures.defaultMapping();
            OrderItemId orderItemId = mapping.orderItemId();

            given(
                            mappingReadManager.getMapping(
                                    DEFAULT_SALES_CHANNEL_ID, DEFAULT_EXTERNAL_PRODUCT_ORDER_ID))
                    .willReturn(mapping);
            given(orderItemReadManager.findAllByIds(List.of(orderItemId))).willReturn(List.of());

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID, externalProductOrderIds);

            // then
            then(confirmOrderUseCase).should(never()).execute(any(OrderItemStatusCommand.class));
        }
    }
}
