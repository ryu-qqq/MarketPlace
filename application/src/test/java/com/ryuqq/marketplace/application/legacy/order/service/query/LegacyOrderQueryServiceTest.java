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
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegacyOrderQueryServiceTest {

    @Mock private LegacyOrderIdResolver idResolver;
    @Mock private GetOrderDetailUseCase getOrderDetailUseCase;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Mock private LegacyOrderFromMarketAssembler assembler;

    @InjectMocks private LegacyOrderQueryService service;

    @Test
    @DisplayName("매핑이 있으면 market에서 조회하여 변환")
    void queryFromMarket() {
        // given
        long legacyOrderId = 5001L;
        LegacyOrderIdMapping mapping = LegacyConversionFixtures.orderMapping();
        ProductOrderDetailResult detail = createDetail("CONFIRMED");

        LegacyOrderDetailResult legacyResult = createLegacyResult(mapping.legacyOrderId());

        given(idResolver.resolve(legacyOrderId)).willReturn(Optional.of(mapping));
        given(getOrderDetailUseCase.execute(mapping.internalOrderItemId())).willReturn(detail);
        given(shipmentReadManager.findByOrderItemId(any(OrderItemId.class)))
                .willReturn(Optional.empty());
        given(assembler.toDetailResult(any(), any(), any())).willReturn(legacyResult);

        // when
        LegacyOrderDetailResult result = service.execute(legacyOrderId);

        // then
        assertThat(result.orderId()).isEqualTo(mapping.legacyOrderId());
    }

    @Test
    @DisplayName("매핑이 없으면 orderId를 market orderItemId로 직접 조회 (fallback)")
    void fallbackWhenNoMapping() {
        long orderId = 9999L;
        given(idResolver.resolve(orderId)).willReturn(Optional.empty());

        ProductOrderDetailResult detail = createDetail("ORDERED");
        given(getOrderDetailUseCase.execute(orderId)).willReturn(detail);
        given(shipmentReadManager.findByOrderItemId(any())).willReturn(Optional.empty());
        given(assembler.toDetailResult(any(), any(), any())).willReturn(createLegacyResult(orderId));

        LegacyOrderDetailResult result = service.execute(orderId);
        assertThat(result).isNotNull();
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

    private LegacyOrderDetailResult createLegacyResult(long orderId) {
        return new LegacyOrderDetailResult(
                orderId,
                1L,
                1L,
                1L,
                1L,
                10000L,
                "DELIVERY_COMPLETED",
                1,
                Instant.now(),
                100L,
                "상품",
                1L,
                "브랜드",
                1L,
                10000L,
                10000L,
                12.0,
                100.0,
                List.of(),
                "img.jpg",
                "수령인",
                "010",
                "12345",
                "주소",
                "상세",
                "요청",
                "",
                "",
                "",
                null,
                "",
                10000L,
                0L,
                "",
                "",
                "",
                "DELIVERY_COMPLETED",
                null,
                "",
                "REFER_DETAIL",
                "",
                "",
                "",
                "MENUAL",
                "OPTION_ONE",
                "NEW",
                "",
                "",
                0L,
                0);
    }
}
