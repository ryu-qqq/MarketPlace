package com.ryuqq.marketplace.application.legacy.order.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.legacy.order.assembler.LegacyOrderFromMarketAssembler;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderPageResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.DeliveryInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.OrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.PaymentInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.port.in.query.GetProductOrderListUseCase;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegacyOrderListQueryServiceTest {

    @Mock private GetProductOrderListUseCase getProductOrderListUseCase;
    @Mock private LegacyOrderIdMappingReadManager mappingReadManager;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Spy private LegacyOrderFromMarketAssembler assembler;

    @InjectMocks private LegacyOrderListQueryService service;

    @Test
    @DisplayName("market에서 조회 후 레거시 형식으로 변환")
    void convertFromMarket() {
        // given
        LegacyOrderSearchParams params = defaultParams();
        ProductOrderListResult item = createListItem(1001L);
        ProductOrderPageResult pageResult =
                new ProductOrderPageResult(List.of(item), PageMeta.of(0, 20, 1));

        LegacyOrderIdMapping mapping =
                LegacyOrderIdMapping.forNew(
                        5001L, 9001L, "order-uuid", 1001L, 1L, "SETOF", Instant.now());

        given(getProductOrderListUseCase.execute(any())).willReturn(pageResult);
        given(mappingReadManager.findByInternalOrderItemIds(List.of(1001L)))
                .willReturn(List.of(mapping));
        given(shipmentReadManager.findByOrderItemIds(anyList())).willReturn(List.of());

        // when
        LegacyOrderPageResult result = service.execute(params);

        // then
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().order().orderId()).isEqualTo(5001L);
        assertThat(result.items().getFirst().order().orderStatus()).isEqualTo("DELIVERY_PENDING");
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("market 결과 없으면 빈 결과 반환")
    void emptyResultWhenNoData() {
        // given
        LegacyOrderSearchParams params = defaultParams();
        ProductOrderPageResult emptyResult =
                new ProductOrderPageResult(List.of(), PageMeta.of(0, 20, 0));

        given(getProductOrderListUseCase.execute(any())).willReturn(emptyResult);

        // when
        LegacyOrderPageResult result = service.execute(params);

        // then
        assertThat(result.items()).isEmpty();
        assertThat(result.totalElements()).isZero();
        verify(mappingReadManager, never()).findByInternalOrderItemIds(anyList());
    }

    @Test
    @DisplayName("매핑 없는 아이템은 건너뜀")
    void skipItemsWithoutMapping() {
        // given
        LegacyOrderSearchParams params = defaultParams();
        ProductOrderListResult item1 = createListItem(1001L);
        ProductOrderListResult item2 = createListItem(1002L);
        ProductOrderPageResult pageResult =
                new ProductOrderPageResult(List.of(item1, item2), PageMeta.of(0, 20, 2));

        // item1만 매핑 존재
        LegacyOrderIdMapping mapping =
                LegacyOrderIdMapping.forNew(
                        5001L, 9001L, "order-uuid", 1001L, 1L, "SETOF", Instant.now());

        given(getProductOrderListUseCase.execute(any())).willReturn(pageResult);
        given(mappingReadManager.findByInternalOrderItemIds(List.of(1001L, 1002L)))
                .willReturn(List.of(mapping));
        given(shipmentReadManager.findByOrderItemIds(anyList())).willReturn(List.of());

        // when
        LegacyOrderPageResult result = service.execute(params);

        // then
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().order().orderId()).isEqualTo(5001L);
    }

    private LegacyOrderSearchParams defaultParams() {
        return new LegacyOrderSearchParams(
                null,
                null,
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 28, 23, 59),
                10L,
                20);
    }

    private ProductOrderListResult createListItem(Long orderItemId) {
        return new ProductOrderListResult(
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
                        orderItemId,
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
                new DeliveryInfo("CONFIRMED", null),
                CancelSummary.none(1),
                ClaimSummary.none(1));
    }
}
