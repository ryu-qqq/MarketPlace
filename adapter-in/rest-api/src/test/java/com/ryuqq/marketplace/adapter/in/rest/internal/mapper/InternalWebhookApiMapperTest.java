package com.ryuqq.marketplace.adapter.in.rest.internal.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCancelledWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCreatedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnRequestedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnWithdrawnWebhookRequest;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InternalWebhookApiMapper 단위 테스트")
class InternalWebhookApiMapperTest {

    private InternalWebhookApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InternalWebhookApiMapper();
    }

    @Nested
    @DisplayName("toExternalOrderPayload() - 주문 생성 요청 변환")
    class ToExternalOrderPayloadTest {

        @Test
        @DisplayName("OrderCreatedWebhookRequest를 ExternalOrderPayload로 변환한다")
        void toExternalOrderPayload_ConvertsRequest_ReturnsPayload() {
            // given
            OrderCreatedWebhookRequest request = InternalWebhookApiFixtures.orderCreatedRequest();

            // when
            ExternalOrderPayload result = mapper.toExternalOrderPayload(request);

            // then
            assertThat(result.externalOrderNo())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_NO);
            assertThat(result.orderedAt()).isEqualTo(InternalWebhookApiFixtures.DEFAULT_ORDERED_AT);
            assertThat(result.buyerName()).isEqualTo("홍길동");
            assertThat(result.buyerEmail()).isEqualTo("buyer@example.com");
            assertThat(result.buyerPhone()).isEqualTo("010-1234-5678");
            assertThat(result.paymentMethod()).isEqualTo("CARD");
            assertThat(result.totalPaymentAmount()).isEqualTo(30000);
            assertThat(result.paidAt()).isEqualTo(InternalWebhookApiFixtures.DEFAULT_PAID_AT);
            assertThat(result.items()).hasSize(1);
        }

        @Test
        @DisplayName("아이템 목록이 올바르게 변환된다")
        void toExternalOrderPayload_ConvertsItems_ReturnsPayloadWithItems() {
            // given
            OrderCreatedWebhookRequest request = InternalWebhookApiFixtures.orderCreatedRequest();

            // when
            ExternalOrderPayload result = mapper.toExternalOrderPayload(request);

            // then
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).externalProductOrderId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
            assertThat(result.items().get(0).externalProductId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
            assertThat(result.items().get(0).externalOptionId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_OPTION_ID);
            assertThat(result.items().get(0).externalProductName()).isEqualTo("테스트 상품명");
            assertThat(result.items().get(0).externalOptionName()).isEqualTo("옵션A");
            assertThat(result.items().get(0).unitPrice()).isEqualTo(30000);
            assertThat(result.items().get(0).quantity()).isEqualTo(1);
            assertThat(result.items().get(0).receiverName()).isEqualTo("김수령");
            assertThat(result.items().get(0).receiverZipCode()).isEqualTo("12345");
        }
    }

    @Nested
    @DisplayName("toExternalClaimPayloads(OrderCancelledWebhookRequest) - 취소 요청 변환")
    class ToExternalClaimPayloadsFromCancelledTest {

        @Test
        @DisplayName("OrderCancelledWebhookRequest를 ExternalClaimPayload 목록으로 변환한다")
        void toExternalClaimPayloads_ConvertsCancel_ReturnsPayloads() {
            // given
            OrderCancelledWebhookRequest request =
                    InternalWebhookApiFixtures.orderCancelledRequest();

            // when
            List<ExternalClaimPayload> result = mapper.toExternalClaimPayloads(request);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalOrderId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_ID);
            assertThat(result.get(0).externalProductOrderId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
            assertThat(result.get(0).claimType()).isEqualTo("CANCEL");
            assertThat(result.get(0).claimStatus()).isEqualTo("CANCEL_REQUEST");
            assertThat(result.get(0).claimReason()).isEqualTo("고객 변심");
            assertThat(result.get(0).claimDetailedReason()).isEqualTo("다른 상품으로 구매 예정");
            assertThat(result.get(0).requestQuantity()).isEqualTo(1);
            assertThat(result.get(0).requestChannel()).isEqualTo("BUYER");
        }

        @Test
        @DisplayName("여러 취소 아이템이 각각 ExternalClaimPayload로 변환된다")
        void toExternalClaimPayloads_MultipleItems_ReturnsMultiplePayloads() {
            // given
            List<OrderCancelledWebhookRequest.CancelledItemRequest> items =
                    List.of(
                            new OrderCancelledWebhookRequest.CancelledItemRequest(
                                    "EXT-PROD-ORDER-001", "고객 변심", null, 1),
                            new OrderCancelledWebhookRequest.CancelledItemRequest(
                                    "EXT-PROD-ORDER-002", "단순 변심", null, 2));
            OrderCancelledWebhookRequest request =
                    new OrderCancelledWebhookRequest(
                            InternalWebhookApiFixtures.DEFAULT_SALES_CHANNEL_ID,
                            InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_ID,
                            items);

            // when
            List<ExternalClaimPayload> result = mapper.toExternalClaimPayloads(request);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).externalProductOrderId()).isEqualTo("EXT-PROD-ORDER-001");
            assertThat(result.get(1).externalProductOrderId()).isEqualTo("EXT-PROD-ORDER-002");
            assertThat(result.get(1).requestQuantity()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("toExternalClaimPayloads(ReturnRequestedWebhookRequest) - 반품 요청 변환")
    class ToExternalClaimPayloadsFromReturnRequestedTest {

        @Test
        @DisplayName("ReturnRequestedWebhookRequest를 ExternalClaimPayload 목록으로 변환한다")
        void toExternalClaimPayloads_ConvertsReturn_ReturnsPayloads() {
            // given
            ReturnRequestedWebhookRequest request =
                    InternalWebhookApiFixtures.returnRequestedRequest();

            // when
            List<ExternalClaimPayload> result = mapper.toExternalClaimPayloads(request);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalOrderId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_ID);
            assertThat(result.get(0).externalProductOrderId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
            assertThat(result.get(0).claimType()).isEqualTo("RETURN");
            assertThat(result.get(0).claimStatus()).isEqualTo("RETURN_REQUEST");
            assertThat(result.get(0).claimReason()).isEqualTo("상품 불량");
            assertThat(result.get(0).claimDetailedReason()).isEqualTo("수령 후 파손 확인");
            assertThat(result.get(0).requestQuantity()).isEqualTo(1);
            assertThat(result.get(0).requestChannel()).isEqualTo("BUYER");
            assertThat(result.get(0).collectDeliveryCompany()).isEqualTo("CJ대한통운");
            assertThat(result.get(0).collectTrackingNumber()).isEqualTo("1234567890123");
        }
    }

    @Nested
    @DisplayName("toExternalClaimPayloads(ReturnWithdrawnWebhookRequest) - 반품 철회 변환")
    class ToExternalClaimPayloadsFromReturnWithdrawnTest {

        @Test
        @DisplayName("ReturnWithdrawnWebhookRequest를 ExternalClaimPayload 목록으로 변환한다")
        void toExternalClaimPayloads_ConvertsWithdrawn_ReturnsPayloads() {
            // given
            ReturnWithdrawnWebhookRequest request =
                    InternalWebhookApiFixtures.returnWithdrawnRequest();

            // when
            List<ExternalClaimPayload> result = mapper.toExternalClaimPayloads(request);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).externalOrderId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_ID);
            assertThat(result.get(0).externalProductOrderId())
                    .isEqualTo(InternalWebhookApiFixtures.DEFAULT_EXTERNAL_PRODUCT_ORDER_ID);
            assertThat(result.get(0).claimType()).isEqualTo("RETURN");
            assertThat(result.get(0).claimStatus()).isEqualTo("RETURN_REJECT");
            assertThat(result.get(0).requestChannel()).isEqualTo("BUYER");
            assertThat(result.get(0).claimReason()).isNull();
            assertThat(result.get(0).claimDetailedReason()).isNull();
            assertThat(result.get(0).requestQuantity()).isNull();
        }
    }
}
