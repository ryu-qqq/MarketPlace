package com.ryuqq.marketplace.application.shipment.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.order.OrderQueryFixtures;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentAssembler 단위 테스트")
class ShipmentAssemblerTest {

    private ShipmentAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ShipmentAssembler();
    }

    @Nested
    @DisplayName(
            "toListResult() - Shipment + OrderItemResult + OrderListResult → ShipmentListResult 변환")
    class ToListResultTest {

        @Test
        @DisplayName("유효한 도메인 객체들로 ShipmentListResult를 생성한다")
        void toListResult_ValidDomainObjects_ReturnsShipmentListResult() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();
            OrderItemResult item = OrderQueryFixtures.orderItemResult();
            OrderListResult order = OrderQueryFixtures.orderListResult();

            // when
            ShipmentListResult result = sut.toListResult(shipment, item, order);

            // then
            assertThat(result).isNotNull();
            assertThat(result.shipment()).isNotNull();
            assertThat(result.shipment().shipmentId()).isEqualTo(shipment.idValue());
            assertThat(result.shipment().status()).isEqualTo(ShipmentStatus.PREPARING.name());
            assertThat(result.order()).isNotNull();
            assertThat(result.order().orderId()).isEqualTo(order.orderId());
            assertThat(result.productOrder()).isNotNull();
            assertThat(result.productOrder().orderItemId()).isEqualTo(item.orderItemId());
            assertThat(result.receiver()).isNotNull();
            assertThat(result.receiver().receiverName()).isEqualTo(item.receiverName());
        }

        @Test
        @DisplayName("송장이 등록된 배송의 경우 택배사 정보가 포함된다")
        void toListResult_ShippedShipment_IncludesCourierInfo() {
            // given
            Shipment shipment = ShipmentFixtures.shippedShipment();
            OrderItemResult item = OrderQueryFixtures.orderItemResult();
            OrderListResult order = OrderQueryFixtures.orderListResult();

            // when
            ShipmentListResult result = sut.toListResult(shipment, item, order);

            // then
            assertThat(result.shipment().trackingNumber()).isEqualTo("1234567890");
            assertThat(result.shipment().courierCode()).isEqualTo("CJ");
            assertThat(result.shipment().courierName()).isEqualTo("CJ대한통운");
        }

        @Test
        @DisplayName("READY 상태 배송의 경우 택배사 정보가 null이다")
        void toListResult_ReadyShipment_NullCourierInfo() {
            // given
            Shipment shipment = ShipmentFixtures.readyShipment();
            OrderItemResult item = OrderQueryFixtures.orderItemResult();
            OrderListResult order = OrderQueryFixtures.orderListResult();

            // when
            ShipmentListResult result = sut.toListResult(shipment, item, order);

            // then
            assertThat(result.shipment().trackingNumber()).isNull();
            assertThat(result.shipment().courierCode()).isNull();
            assertThat(result.shipment().courierName()).isNull();
        }
    }

    @Nested
    @DisplayName("toDetailResult() - Shipment + ProductOrderDetailData → ShipmentDetailResult 변환")
    class ToDetailResultTest {

        @Test
        @DisplayName("결제 정보가 있는 경우 ShipmentDetailResult에 PaymentInfo가 포함된다")
        void toDetailResult_WithPayment_IncludesPaymentInfo() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();
            OrderItemResult item = OrderQueryFixtures.orderItemResult();
            OrderListResult order = OrderQueryFixtures.orderListResult();
            PaymentResult payment = OrderQueryFixtures.paymentResult();
            ProductOrderDetailData detailData = new ProductOrderDetailData(item, order, payment);

            // when
            ShipmentDetailResult result = sut.toDetailResult(shipment, detailData);

            // then
            assertThat(result).isNotNull();
            assertThat(result.shipment().shipmentId()).isEqualTo(shipment.idValue());
            assertThat(result.payment()).isNotNull();
            assertThat(result.payment().paymentStatus()).isEqualTo("PAID");
            assertThat(result.payment().paymentAmount()).isEqualTo(20000);
        }

        @Test
        @DisplayName("결제 정보가 없는 경우 PaymentInfo가 null이다")
        void toDetailResult_WithoutPayment_NullPaymentInfo() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();
            OrderItemResult item = OrderQueryFixtures.orderItemResult();
            OrderListResult order = OrderQueryFixtures.orderListResult();
            ProductOrderDetailData detailData = new ProductOrderDetailData(item, order, null);

            // when
            ShipmentDetailResult result = sut.toDetailResult(shipment, detailData);

            // then
            assertThat(result.payment()).isNull();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("배송 목록과 페이지 정보로 ShipmentPageResult를 생성한다")
        void toPageResult_ValidParams_ReturnsShipmentPageResult() {
            // given
            Shipment shipment = ShipmentFixtures.preparingShipment();
            OrderItemResult item = OrderQueryFixtures.orderItemResult();
            OrderListResult order = OrderQueryFixtures.orderListResult();
            ShipmentListResult listResult = sut.toListResult(shipment, item, order);

            // when
            ShipmentPageResult result = sut.toPageResult(List.of(listResult), 0, 20, 1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(1L);
            assertThat(result.pageMeta().page()).isZero();
            assertThat(result.pageMeta().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("빈 목록으로 빈 ShipmentPageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // when
            ShipmentPageResult result = sut.toPageResult(List.of(), 0, 20, 0L);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toSummaryResult() - 상태별 카운트 → ShipmentSummaryResult 변환")
    class ToSummaryResultTest {

        @Test
        @DisplayName("상태별 카운트 맵을 ShipmentSummaryResult로 변환한다")
        void toSummaryResult_ValidCounts_ReturnsSummaryResult() {
            // given
            Map<ShipmentStatus, Long> statusCounts = new EnumMap<>(ShipmentStatus.class);
            statusCounts.put(ShipmentStatus.READY, 5L);
            statusCounts.put(ShipmentStatus.PREPARING, 3L);
            statusCounts.put(ShipmentStatus.SHIPPED, 10L);
            statusCounts.put(ShipmentStatus.IN_TRANSIT, 7L);
            statusCounts.put(ShipmentStatus.DELIVERED, 2L);
            statusCounts.put(ShipmentStatus.FAILED, 1L);
            statusCounts.put(ShipmentStatus.CANCELLED, 0L);

            // when
            ShipmentSummaryResult result = sut.toSummaryResult(statusCounts);

            // then
            assertThat(result.ready()).isEqualTo(5);
            assertThat(result.preparing()).isEqualTo(3);
            assertThat(result.shipped()).isEqualTo(10);
            assertThat(result.inTransit()).isEqualTo(7);
            assertThat(result.delivered()).isEqualTo(2);
            assertThat(result.failed()).isEqualTo(1);
            assertThat(result.cancelled()).isZero();
        }

        @Test
        @DisplayName("빈 맵이면 모든 카운트가 0인 SummaryResult를 반환한다")
        void toSummaryResult_EmptyMap_ReturnsZeroCounts() {
            // given
            Map<ShipmentStatus, Long> emptyMap = Map.of();

            // when
            ShipmentSummaryResult result = sut.toSummaryResult(emptyMap);

            // then
            assertThat(result.ready()).isZero();
            assertThat(result.preparing()).isZero();
            assertThat(result.shipped()).isZero();
            assertThat(result.inTransit()).isZero();
            assertThat(result.delivered()).isZero();
            assertThat(result.failed()).isZero();
            assertThat(result.cancelled()).isZero();
        }

        @Test
        @DisplayName("일부 상태만 있는 맵이면 나머지 상태는 0으로 처리된다")
        void toSummaryResult_PartialMap_DefaultsToZero() {
            // given
            Map<ShipmentStatus, Long> partialMap =
                    Map.of(
                            ShipmentStatus.READY, 10L,
                            ShipmentStatus.SHIPPED, 5L);

            // when
            ShipmentSummaryResult result = sut.toSummaryResult(partialMap);

            // then
            assertThat(result.ready()).isEqualTo(10);
            assertThat(result.shipped()).isEqualTo(5);
            assertThat(result.preparing()).isZero();
            assertThat(result.inTransit()).isZero();
        }
    }
}
