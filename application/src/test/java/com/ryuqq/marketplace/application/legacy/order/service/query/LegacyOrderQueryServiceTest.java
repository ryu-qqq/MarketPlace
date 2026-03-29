package com.ryuqq.marketplace.application.legacy.order.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.legacy.order.assembler.LegacyOrderFromMarketAssembler;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.resolver.LegacyOrderIdResolver;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.DeliveryInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.OrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.PaymentInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegacyOrderQueryServiceTest {

    @Mock private LegacyOrderIdResolver idResolver;
    @Mock private GetOrderDetailUseCase getOrderDetailUseCase;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Spy private LegacyOrderFromMarketAssembler assembler;

    @InjectMocks private LegacyOrderQueryService service;

    @Test
    @DisplayName("매핑이 있으면 market에서 조회하여 변환")
    void queryFromMarket() {
        // given
        long legacyOrderId = 5001L;
        LegacyOrderIdMapping mapping = LegacyConversionFixtures.orderMapping();
        ProductOrderDetailResult detail = createDetail("CONFIRMED");

        given(idResolver.resolve(legacyOrderId)).willReturn(Optional.of(mapping));
        given(getOrderDetailUseCase.execute(mapping.internalOrderItemId())).willReturn(detail);
        given(shipmentReadManager.findByOrderItemId(any(OrderItemId.class)))
                .willReturn(Optional.empty());

        // when
        LegacyOrderDetailResult result = service.execute(legacyOrderId);

        // then
        assertThat(result.orderId()).isEqualTo(mapping.legacyOrderId());
        assertThat(result.paymentId()).isEqualTo(mapping.legacyPaymentId());
        assertThat(result.orderStatus()).isEqualTo("DELIVERY_PENDING");
    }

    @Test
    @DisplayName("매핑이 없으면 예외 발생")
    void throwsWhenNoMapping() {
        long legacyOrderId = 9999L;
        given(idResolver.resolve(legacyOrderId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(legacyOrderId))
                .isInstanceOf(
                        com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException.class);
    }

    private ProductOrderDetailResult createDetail(String orderItemStatus) {
        return new ProductOrderDetailResult(
                new OrderInfo(
                        "order-uuid",
                        "ORD-001",
                        1L,
                        10L,
                        "SETOF",
                        "세토프",
                        null,
                        null,
                        "홍길동",
                        "buyer@test.com",
                        "010-0000-0000",
                        Instant.now(),
                        Instant.now()),
                new ProductOrderInfo(
                        1001L,
                        "ORD-001-001",
                        100L,
                        1L,
                        1L,
                        null,
                        200L,
                        "SKU-001",
                        "상품그룹",
                        "브랜드",
                        "셀러",
                        "img.jpg",
                        null,
                        null,
                        null,
                        "블랙/L",
                        null,
                        10000,
                        10000,
                        1,
                        10000,
                        0,
                        10000),
                new PaymentInfo(
                        "pay-uuid", "PAY-001", "PAID", "CARD", null, 10000, Instant.now(), null),
                new ReceiverInfo("김수령", "010-1111-2222", "12345", "서울시 강남구", "101호", "문앞"),
                new DeliveryInfo(orderItemStatus, null),
                CancelSummary.none(1),
                ClaimSummary.none(1),
                List.of(),
                List.of(),
                List.of());
    }
}
