package com.ryuqq.marketplace.adapter.out.client.sellic.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicOrderQueryResponse.SellicOrderData;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellicCommerceOrderMapper 단위 테스트")
class SellicCommerceOrderMapperTest {

    private final SellicCommerceOrderMapper sut = new SellicCommerceOrderMapper();

    // ── 헬퍼 메서드 ──

    private SellicOrderData createOrderData(
            int idx, String orderId, String productName, int saleCost, int saleCnt) {
        return new SellicOrderData(
                idx,                         // 1  IDX
                orderId,                     // 2  ORDER_ID
                null,                        // 3  ORDER_SUB_ID
                null,                        // 4  ORIGINAL_ORDER_ID
                2000,                        // 5  ORDER_STATUS
                "2026-03-20 10:00:00",       // 6  ORDER_DATE
                "2026-03-20 10:01:00",       // 7  CREATED_AT
                null,                        // 8  ORDER_TYPE
                "홍길동",                     // 9  USER_NAME
                "02-1234-5678",              // 10 USER_TEL
                "010-1234-5678",             // 11 USER_CEL
                "김철수",                     // 12 RECEIVE_NAME
                null,                        // 13 RECEIVE_TEL
                "010-9999-8888",             // 14 RECEIVE_CEL
                "12345",                     // 15 RECEIVE_ZIPCODE
                "서울시 강남구 테헤란로",       // 16 RECEIVE_ADDR
                "부재시 경비실",               // 17 DELV_MSG
                saleCost,                    // 18 SALE_COST
                null,                        // 19 MALL_WON_COST
                saleCnt,                     // 20 SALE_CNT
                saleCost * saleCnt,          // 21 TOTAL_PRICE
                null,                        // 22 SETTLEMENT_PRICE
                saleCost * saleCnt,          // 23 PAYMENT_PRICE
                0,                           // 24 DELIVERY_FEE
                100,                         // 25 PRODUCT_ID
                200,                         // 26 OPTION_CODE
                null,                        // 27 MALL_PRODUCT_ID
                productName,                 // 28 PRODUCT_NAME
                "옵션A",                     // 29 OPTION_NAME
                "SKU001",                    // 30 OWN_CODE
                null,                        // 31 SELLIC_PRODUCT_NAME
                null,                        // 32 OPTION_ITEM
                null,                        // 33 STOCK_BARCODE
                null,                        // 34 DELIVERY
                null,                        // 35 INVOICE
                null,                        // 36 MALL_ID
                null,                        // 37 MALL_NAME
                null);                       // 38 ORDER_CHANNEL
    }

    @Nested
    @DisplayName("toExternalOrderPayloads()")
    class ToExternalOrderPayloads {

        @Test
        @DisplayName("null 입력은 빈 리스트를 반환한다")
        void nullReturnsEmpty() {
            assertThat(sut.toExternalOrderPayloads(null)).isEmpty();
        }

        @Test
        @DisplayName("빈 리스트 입력은 빈 리스트를 반환한다")
        void emptyReturnsEmpty() {
            assertThat(sut.toExternalOrderPayloads(List.of())).isEmpty();
        }

        @Test
        @DisplayName("단일 주문을 변환한다")
        void singleOrder() {
            var datas = List.of(createOrderData(1, "ORD001", "테스트 상품", 50000, 2));

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(datas);

            assertThat(result).hasSize(1);
            var order = result.get(0);
            assertThat(order.externalOrderNo()).isEqualTo("ORD001");
            assertThat(order.buyerName()).isEqualTo("홍길동");
            assertThat(order.buyerPhone()).isEqualTo("010-1234-5678");
            assertThat(order.orderedAt()).isNotNull();
            assertThat(order.totalPaymentAmount()).isEqualTo(100000);

            var item = order.items().get(0);
            assertThat(item.externalProductOrderId()).isEqualTo("1");
            assertThat(item.externalProductName()).isEqualTo("테스트 상품");
            assertThat(item.unitPrice()).isEqualTo(50000);
            assertThat(item.quantity()).isEqualTo(2);
            assertThat(item.receiverName()).isEqualTo("김철수");
            assertThat(item.receiverPhone()).isEqualTo("010-9999-8888");
        }

        @Test
        @DisplayName("같은 ORDER_ID는 하나의 주문으로 그룹핑한다")
        void groupsBySameOrderId() {
            var datas =
                    List.of(
                            createOrderData(1, "ORD001", "상품A", 30000, 1),
                            createOrderData(2, "ORD001", "상품B", 20000, 1));

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(datas);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).items()).hasSize(2);
            assertThat(result.get(0).totalPaymentAmount()).isEqualTo(50000);
        }

        @Test
        @DisplayName("ORDER_ID가 null이면 IDX를 주문키로 사용한다")
        void fallbackToIdxWhenOrderIdNull() {
            var data =
                    new SellicOrderData(
                            999, null, null, null, 2000,
                            "2026-03-20 10:00:00", null, null,
                            "주문자", null, null, "수령인",
                            null, null, null, null, null,
                            10000, null, 1, 10000, null, 10000, 0,
                            100, null, null, "상품", null, null,
                            null, null, null, null, null, null, null, null);

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(List.of(data));

            assertThat(result.get(0).externalOrderNo()).isEqualTo("999");
        }

        @Test
        @DisplayName("날짜 파싱 실패 시 null로 처리한다")
        void invalidDateReturnsNull() {
            var data =
                    new SellicOrderData(
                            1, "ORD001", null, null, 2000,
                            "invalid-date", null, null,
                            "주문자", null, null, null,
                            null, null, null, null, null,
                            10000, null, 1, 10000, null, 10000, 0,
                            100, null, null, "상품", null, null,
                            null, null, null, null, null, null, null, null);

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(List.of(data));

            // 날짜 파싱 실패해도 변환은 성공해야 한다
            assertThat(result).hasSize(1);
        }
    }
}
