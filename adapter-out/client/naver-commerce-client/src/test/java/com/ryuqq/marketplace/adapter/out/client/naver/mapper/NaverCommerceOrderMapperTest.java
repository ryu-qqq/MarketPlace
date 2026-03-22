package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderOrder;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverShippingAddress;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverCommerceOrderMapper 단위 테스트")
class NaverCommerceOrderMapperTest {

    private final NaverCommerceOrderMapper sut = new NaverCommerceOrderMapper();

    // ── 헬퍼 메서드 ──

    private NaverProductOrderOrder createOrder(String orderId) {
        return new NaverProductOrderOrder(
                orderId,
                "2026-03-20T10:00:00+09:00",
                "2026-03-20T10:01:00+09:00",
                "홍길동",
                "010-1234-5678",
                "buyer***",
                "12345",
                "MOBILE",
                "신용카드",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private NaverProductOrderDetail.ProductOrderInfo createProductOrderInfo(
            String productOrderId, int unitPrice, int quantity) {
        NaverShippingAddress addr =
                new NaverShippingAddress(
                        "김철수", "010-9999-8888", null, "12345", "서울시 강남구", "101동", "ROAD", true);
        return new NaverProductOrderDetail.ProductOrderInfo(
                productOrderId, // 1  productOrderId
                "PAYED", // 2  productOrderStatus
                null,
                null,
                null, // 3-5  claimStatus, claimType, claimId
                null,
                null,
                null, // 6-8  placeOrderDate, placeOrderStatus, decisionDate
                null,
                null, // 9-10 productClass, groupProductId
                "PROD001",
                null, // 11-12 productId, originalProductId
                "테스트 상품",
                "옵션A", // 13-14 productName, productOption
                "OPT001",
                null, // 15-16 optionCode, optionPrice
                null,
                null, // 17-18 sellerProductCode, optionManageCode
                null,
                null, // 19-20 itemNo, mallId
                quantity,
                null,
                null, // 21-23 quantity, initialQuantity, remainQuantity
                unitPrice, // 24 unitPrice
                unitPrice * quantity, // 25 totalProductAmount
                null,
                null, // 26-27 initialProductAmount, remainProductAmount
                0, // 28 productDiscountAmount
                unitPrice * quantity, // 29 totalPaymentAmount
                null,
                null, // 30-31 initialPaymentAmount, remainPaymentAmount
                null,
                null,
                null, // 32-34 sellerBurdenDiscountAmount, deliveryFeeAmount, deliveryDiscountAmount
                null,
                null,
                null, // 35-37 deliveryPolicyType, shippingFeeType, packageNumber
                null,
                null,
                null, // 38-40 expectedDeliveryMethod, expectedDeliveryCompany,
                // deliveryAttributeType
                null,
                null, // 41-42 shippingStartDate, shippingDueDate
                null,
                null,
                null,
                null, // 43-46 commissionRatingType, paymentCommission, saleCommission,
                // channelCommission
                null,
                null,
                null, // 47-49 expectedSettlementAmount, inflowPath, taxType
                addr, // 50 shippingAddress
                "부재시 경비실", // 51 shippingMemo
                null, // 52 freeGift
                null, // 53 currentClaim
                null); // 54 completedClaims
    }

    private NaverProductOrderDetail createDetail(
            String orderId, String productOrderId, int unitPrice, int quantity) {
        return new NaverProductOrderDetail(
                createOrder(orderId),
                createProductOrderInfo(productOrderId, unitPrice, quantity),
                null);
    }

    @Nested
    @DisplayName("toExternalOrderPayloads()")
    class ToExternalOrderPayloads {

        @Test
        @DisplayName("단일 주문 단일 아이템을 변환한다")
        void singleOrderSingleItem() {
            var details = List.of(createDetail("ORD001", "PO001", 50000, 2));

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(details);

            assertThat(result).hasSize(1);
            var order = result.get(0);
            assertThat(order.externalOrderNo()).isEqualTo("ORD001");
            assertThat(order.buyerName()).isEqualTo("홍길동");
            assertThat(order.buyerPhone()).isEqualTo("010-1234-5678");
            assertThat(order.paymentMethod()).isEqualTo("신용카드");
            assertThat(order.orderedAt()).isNotNull();
            assertThat(order.paidAt()).isNotNull();
            assertThat(order.items()).hasSize(1);

            var item = order.items().get(0);
            assertThat(item.externalProductOrderId()).isEqualTo("PO001");
            assertThat(item.externalProductId()).isEqualTo("PROD001");
            assertThat(item.unitPrice()).isEqualTo(50000);
            assertThat(item.quantity()).isEqualTo(2);
            assertThat(item.receiverName()).isEqualTo("김철수");
            assertThat(item.receiverPhone()).isEqualTo("010-9999-8888");
            assertThat(item.receiverZipCode()).isEqualTo("12345");
            assertThat(item.deliveryRequest()).isEqualTo("부재시 경비실");
        }

        @Test
        @DisplayName("동일 주문번호의 다중 아이템은 하나의 주문으로 그룹핑한다")
        void multipleItemsSameOrder() {
            var details =
                    List.of(
                            createDetail("ORD001", "PO001", 50000, 1),
                            createDetail("ORD001", "PO002", 30000, 2));

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(details);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).items()).hasSize(2);
            assertThat(result.get(0).totalPaymentAmount()).isEqualTo(110000);
        }

        @Test
        @DisplayName("서로 다른 주문번호는 별도 주문으로 분리한다")
        void differentOrdersSeparated() {
            var details =
                    List.of(
                            createDetail("ORD001", "PO001", 50000, 1),
                            createDetail("ORD002", "PO002", 30000, 1));

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(details);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("배송지 정보가 없어도 null 처리된다")
        void nullShippingAddress() {
            NaverProductOrderDetail.ProductOrderInfo po =
                    new NaverProductOrderDetail.ProductOrderInfo(
                            "PO001", "PAYED", // 1-2
                            null, null, null, // 3-5
                            null, null, null, // 6-8
                            null, null, // 9-10
                            "PROD001", null, // 11-12
                            "상품명", null, // 13-14
                            null, null, // 15-16
                            null, null, // 17-18
                            null, null, // 19-20
                            1, null, null, // 21-23
                            10000, 10000, // 24-25
                            null, null, // 26-27
                            0, 10000, // 28-29
                            null, null, // 30-31
                            null, null, null, // 32-34
                            null, null, null, // 35-37
                            null, null, null, // 38-40
                            null, null, // 41-42
                            null, null, null, null, // 43-46
                            null, null, null, // 47-49
                            null, // 50 shippingAddress
                            null, // 51 shippingMemo
                            null, // 52 freeGift
                            null, // 53 currentClaim
                            null); // 54 completedClaims
            var detail = new NaverProductOrderDetail(createOrder("ORD001"), po, null);

            List<ExternalOrderPayload> result = sut.toExternalOrderPayloads(List.of(detail));

            var item = result.get(0).items().get(0);
            assertThat(item.receiverName()).isNull();
            assertThat(item.receiverPhone()).isNull();
        }
    }
}
