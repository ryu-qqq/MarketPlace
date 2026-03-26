package com.ryuqq.marketplace.application.shipment.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.order.OrderQueryFixtures;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import com.ryuqq.marketplace.application.shipment.ShipmentQueryFixtures;
import com.ryuqq.marketplace.application.shipment.assembler.ShipmentAssembler;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import java.util.Optional;
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
@DisplayName("GetShipmentDetailService 단위 테스트")
class GetShipmentDetailServiceTest {

    @InjectMocks private GetShipmentDetailService sut;

    @Mock private ShipmentReadManager readManager;
    @Mock private OrderCompositionReadManager orderReadManager;
    @Mock private ShipmentAssembler assembler;

    @Nested
    @DisplayName("execute() - 배송 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 shipmentId로 배송 상세 정보를 반환한다")
        void execute_ValidShipmentId_ReturnsShipmentDetailResult() {
            // given
            String shipmentId = "01944b2a-1234-7fff-8888-abcdef012345";
            Shipment shipment = ShipmentFixtures.preparingShipment();
            ProductOrderDetailData detailData =
                    new ProductOrderDetailData(
                            OrderQueryFixtures.orderItemResult(),
                            OrderQueryFixtures.orderListResult(),
                            OrderQueryFixtures.paymentResult());
            ShipmentDetailResult expected = ShipmentQueryFixtures.shipmentDetailResult();

            given(readManager.getById(ShipmentId.of(shipmentId))).willReturn(shipment);
            given(orderReadManager.findProductOrderDetail(shipment.orderItemIdValue()))
                    .willReturn(Optional.of(detailData));
            given(assembler.toDetailResult(shipment, detailData)).willReturn(expected);

            // when
            ShipmentDetailResult result = sut.execute(shipmentId);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().getById(ShipmentId.of(shipmentId));
            then(orderReadManager).should().findProductOrderDetail(shipment.orderItemIdValue());
            then(assembler).should().toDetailResult(shipment, detailData);
        }

        @Test
        @DisplayName("주문 정보를 찾을 수 없으면 OrderNotFoundException이 발생한다")
        void execute_OrderNotFound_ThrowsOrderNotFoundException() {
            // given
            String shipmentId = "01944b2a-1234-7fff-8888-abcdef012345";
            Shipment shipment = ShipmentFixtures.preparingShipment();

            given(readManager.getById(ShipmentId.of(shipmentId))).willReturn(shipment);
            given(orderReadManager.findProductOrderDetail(shipment.orderItemIdValue()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute(shipmentId))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }
}
