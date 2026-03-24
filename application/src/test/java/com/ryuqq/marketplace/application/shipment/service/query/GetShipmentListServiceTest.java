package com.ryuqq.marketplace.application.shipment.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.order.OrderQueryFixtures;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import com.ryuqq.marketplace.application.shipment.ShipmentQueryFixtures;
import com.ryuqq.marketplace.application.shipment.assembler.ShipmentAssembler;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentQueryFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import java.util.List;
import java.util.Map;
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
@DisplayName("GetShipmentListService 단위 테스트")
class GetShipmentListServiceTest {

    @InjectMocks private GetShipmentListService sut;

    @Mock private ShipmentReadManager readManager;
    @Mock private OrderCompositionReadManager orderReadManager;
    @Mock private ShipmentQueryFactory queryFactory;
    @Mock private ShipmentAssembler assembler;

    @Nested
    @DisplayName("execute() - 배송 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("주문 상품이 있으면 ShipmentPageResult를 반환한다")
        void execute_ExistingOrderItems_ReturnsShipmentPageResult() {
            // given
            ShipmentSearchParams params = ShipmentQueryFixtures.searchParams();
            ShipmentSearchCriteria criteria =
                    org.mockito.Mockito.mock(ShipmentSearchCriteria.class);
            String orderItemId = "01940001-0000-7000-8000-000000000001";
            Shipment shipment = ShipmentFixtures.preparingShipment();
            OrderItemResult item =
                    OrderQueryFixtures.orderItemResult(
                            orderItemId, OrderQueryFixtures.DEFAULT_ORDER_ID);
            OrderListResult order = OrderQueryFixtures.orderListResult();
            ShipmentListResult listResult = ShipmentQueryFixtures.shipmentListResult();
            ShipmentPageResult expected = ShipmentQueryFixtures.shipmentPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findFulfillmentOrderItemIds(criteria))
                    .willReturn(List.of(orderItemId));
            given(readManager.countFulfillment(criteria)).willReturn(1L);
            given(orderReadManager.findOrderItemsByIds(any()))
                    .willReturn(Map.of(orderItemId, item));
            given(readManager.findByOrderItemIds(any())).willReturn(List.of(shipment));
            given(orderReadManager.findOrdersByIds(any()))
                    .willReturn(Map.of(item.orderId(), order));
            given(assembler.toListResult(eq(shipment), eq(item), eq(order), eq(true)))
                    .willReturn(listResult);
            given(assembler.toPageResult(List.of(listResult), 0, 20, 1L)).willReturn(expected);

            // when
            ShipmentPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findFulfillmentOrderItemIds(criteria);
            then(readManager).should().countFulfillment(criteria);
        }

        @Test
        @DisplayName("주문 상품이 비어있으면 빈 페이지 결과를 반환한다")
        void execute_EmptyOrderItems_ReturnsEmptyPageResult() {
            // given
            ShipmentSearchParams params = ShipmentQueryFixtures.searchParams();
            ShipmentSearchCriteria criteria =
                    org.mockito.Mockito.mock(ShipmentSearchCriteria.class);
            ShipmentPageResult expected = ShipmentQueryFixtures.emptyShipmentPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findFulfillmentOrderItemIds(criteria)).willReturn(List.of());
            given(readManager.countFulfillment(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), 0, 20, 0L)).willReturn(expected);

            // when
            ShipmentPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            then(orderReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("OrderItem 정보가 없는 건은 결과에서 제외된다")
        void execute_MissingOrderItem_ExcludesFromResult() {
            // given
            ShipmentSearchParams params = ShipmentQueryFixtures.searchParams();
            ShipmentSearchCriteria criteria =
                    org.mockito.Mockito.mock(ShipmentSearchCriteria.class);
            String orderItemId = "01940001-0000-7000-8000-000000000001";
            ShipmentPageResult expected = ShipmentQueryFixtures.emptyShipmentPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findFulfillmentOrderItemIds(criteria))
                    .willReturn(List.of(orderItemId));
            given(readManager.countFulfillment(criteria)).willReturn(1L);
            given(orderReadManager.findOrderItemsByIds(any())).willReturn(Map.of());
            given(readManager.findByOrderItemIds(any())).willReturn(List.of());
            given(orderReadManager.findOrdersByIds(any())).willReturn(Map.of());
            given(assembler.toPageResult(List.of(), 0, 20, 1L)).willReturn(expected);

            // when
            ShipmentPageResult result = sut.execute(params);

            // then
            then(assembler).should().toPageResult(List.of(), 0, 20, 1L);
        }
    }
}
