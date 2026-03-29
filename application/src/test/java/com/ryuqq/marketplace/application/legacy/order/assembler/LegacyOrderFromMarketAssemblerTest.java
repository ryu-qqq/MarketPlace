package com.ryuqq.marketplace.application.legacy.order.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailWithHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary.LatestCancel;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary.LatestClaim;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.DeliveryInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.OrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.PaymentInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ReceiverInfo;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LegacyOrderFromMarketAssemblerTest {

    private LegacyOrderFromMarketAssembler assembler;
    private LegacyOrderIdMapping mapping;

    @BeforeEach
    void setUp() {
        com.ryuqq.marketplace.application.legacyconversion.manager.LegacySellerIdMappingReadManager
                mockManager =
                        org.mockito.Mockito.mock(
                                com.ryuqq.marketplace.application.legacyconversion.manager
                                        .LegacySellerIdMappingReadManager.class);
        org.mockito.Mockito.when(
                        mockManager.findLegacySellerIdByInternalSellerId(
                                org.mockito.ArgumentMatchers.anyLong()))
                .thenAnswer(inv -> java.util.Optional.of(inv.getArgument(0, Long.class)));
        assembler = new LegacyOrderFromMarketAssembler(mockManager);
        mapping =
                LegacyOrderIdMapping.forNew(
                        5001L,
                        9001L,
                        "01900000-0000-7000-8000-000000000001",
                        1001L,
                        1L,
                        "SETOF",
                        Instant.now());
    }

    // ==================== 역방향 상태 매핑 테스트 ====================

    @Nested
    @DisplayName("역방향 상태 매핑")
    class ReverseStatusMapping {

        @Test
        @DisplayName("READY → ORDER_PROCESSING")
        void readyToOrderProcessing() {
            ProductOrderDetailResult detail = detailWith("READY", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, null);
            assertThat(status).isEqualTo("ORDER_PROCESSING");
        }

        @Test
        @DisplayName("CONFIRMED + ShipmentStatus.READY → DELIVERY_PENDING")
        void confirmedWithShipmentReady() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.READY);
            assertThat(status).isEqualTo("DELIVERY_PENDING");
        }

        @Test
        @DisplayName("CONFIRMED + ShipmentStatus.PREPARING → DELIVERY_PENDING")
        void confirmedWithShipmentPreparing() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.PREPARING);
            assertThat(status).isEqualTo("DELIVERY_PENDING");
        }

        @Test
        @DisplayName("CONFIRMED + ShipmentStatus.IN_TRANSIT → DELIVERY_PROCESSING")
        void confirmedWithShipmentInTransit() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.IN_TRANSIT);
            assertThat(status).isEqualTo("DELIVERY_PROCESSING");
        }

        @Test
        @DisplayName("CONFIRMED + ShipmentStatus.SHIPPED → DELIVERY_PROCESSING")
        void confirmedWithShipmentShipped() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.SHIPPED);
            assertThat(status).isEqualTo("DELIVERY_PROCESSING");
        }

        @Test
        @DisplayName("CONFIRMED + ShipmentStatus.DELIVERED → DELIVERY_COMPLETED")
        void confirmedWithShipmentDelivered() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("DELIVERY_COMPLETED");
        }

        @Test
        @DisplayName("CONFIRMED + null shipment → DELIVERY_PENDING")
        void confirmedWithNullShipment() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, null);
            assertThat(status).isEqualTo("DELIVERY_PENDING");
        }

        @Test
        @DisplayName("CANCELLED → SALE_CANCELLED_COMPLETED")
        void cancelledOrderItem() {
            ProductOrderDetailResult detail = detailWith("CANCELLED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("SALE_CANCELLED_COMPLETED");
        }

        @Test
        @DisplayName("RETURNED → RETURN_REQUEST_COMPLETED")
        void returnedOrderItem() {
            ProductOrderDetailResult detail = detailWith("RETURNED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("RETURN_REQUEST_COMPLETED");
        }

        @Test
        @DisplayName("RETURN_REQUESTED → RETURN_REQUEST")
        void returnRequestedOrderItem() {
            ProductOrderDetailResult detail =
                    detailWith("RETURN_REQUESTED", noneCancel(), noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("RETURN_REQUEST");
        }
    }

    @Nested
    @DisplayName("활성 취소 기반 매핑")
    class ActiveCancelMapping {

        @Test
        @DisplayName("활성 취소 REQUESTED → SALE_CANCELLED")
        void activeCancelRequested() {
            CancelSummary cancel = activeCancel("REQUESTED");
            ProductOrderDetailResult detail = detailWith("CONFIRMED", cancel, noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("SALE_CANCELLED");
        }

        @Test
        @DisplayName("활성 취소 APPROVED → CANCEL_REQUEST_CONFIRMED")
        void activeCancelApproved() {
            CancelSummary cancel = activeCancel("APPROVED");
            ProductOrderDetailResult detail = detailWith("CONFIRMED", cancel, noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("CANCEL_REQUEST_CONFIRMED");
        }

        @Test
        @DisplayName("완료 취소 → SALE_CANCELLED_COMPLETED")
        void completedCancelStatus() {
            CancelSummary cancel = completedCancel();
            ProductOrderDetailResult detail = detailWith("CANCELLED", cancel, noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("SALE_CANCELLED_COMPLETED");
        }

        @Test
        @DisplayName("거절된 취소 → 배송 상태 기반 매핑으로 폴백")
        void rejectedCancelFallsBack() {
            CancelSummary cancel =
                    new CancelSummary(
                            true,
                            0,
                            2,
                            new LatestCancel(
                                    "1",
                                    "C-001",
                                    "REJECTED",
                                    1,
                                    Instant.parse("2026-02-19T10:00:00Z")));
            ProductOrderDetailResult detail = detailWith("CONFIRMED", cancel, noneClaim());
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("DELIVERY_COMPLETED");
        }
    }

    @Nested
    @DisplayName("활성 클레임(반품) 기반 매핑")
    class ActiveClaimMapping {

        @Test
        @DisplayName("반품 REQUESTED → RETURN_REQUEST")
        void refundRequested() {
            ClaimSummary claim = activeClaim("REQUESTED");
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), claim);
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("RETURN_REQUEST");
        }

        @Test
        @DisplayName("반품 COLLECTING → RETURN_REQUEST_CONFIRMED")
        void refundCollecting() {
            ClaimSummary claim = activeClaim("COLLECTING");
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), claim);
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("RETURN_REQUEST_CONFIRMED");
        }

        @Test
        @DisplayName("반품 COLLECTED → RETURN_REQUEST_CONFIRMED")
        void refundCollected() {
            ClaimSummary claim = activeClaim("COLLECTED");
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), claim);
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("RETURN_REQUEST_CONFIRMED");
        }

        @Test
        @DisplayName("반품 COMPLETED → RETURN_REQUEST_COMPLETED")
        void refundCompleted() {
            ClaimSummary claim =
                    new ClaimSummary(
                            false,
                            0,
                            1,
                            1,
                            new LatestClaim(
                                    "1",
                                    "CL-001",
                                    "REFUND",
                                    "COMPLETED",
                                    1,
                                    Instant.parse("2026-02-20T10:00:00Z")));
            ProductOrderDetailResult detail = detailWith("RETURNED", noneCancel(), claim);
            // 활성 클레임 없으므로 OrderItemStatus 기반 → RETURNED
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("RETURN_REQUEST_COMPLETED");
        }

        @Test
        @DisplayName("반품 REJECTED → RETURN_REQUEST_REJECTED")
        void refundRejected() {
            ClaimSummary claim = activeClaim("REJECTED");
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), claim);
            String status = assembler.resolveOrderStatus(detail, ShipmentStatus.DELIVERED);
            assertThat(status).isEqualTo("RETURN_REQUEST_REJECTED");
        }
    }

    // ==================== 전체 변환 테스트 ====================

    @Nested
    @DisplayName("전체 변환")
    class FullConversion {

        @Test
        @DisplayName("toDetailResult: 필드 매핑 검증")
        void toDetailResultFieldMapping() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());

            LegacyOrderDetailResult result =
                    assembler.toDetailResult(
                            detail,
                            mapping,
                            (com.ryuqq.marketplace.domain.shipment.aggregate.Shipment) null);

            assertThat(result.orderId()).isEqualTo(5001L);
            assertThat(result.paymentId()).isEqualTo(9001L);
            assertThat(result.productId()).isEqualTo(200L);
            assertThat(result.sellerId()).isEqualTo(1L);
            assertThat(result.orderStatus()).isEqualTo("DELIVERY_COMPLETED");
            assertThat(result.quantity()).isEqualTo(2);
            assertThat(result.productGroupId()).isEqualTo(100L);
            assertThat(result.productGroupName()).isEqualTo("테스트 상품그룹");
            assertThat(result.brandName()).isEqualTo("테스트 브랜드");
            assertThat(result.mainImageUrl()).isEqualTo("https://example.com/images/main.jpg");
            assertThat(result.receiverName()).isEqualTo("김수령");
            assertThat(result.receiverPhone()).isEqualTo("010-9876-5432");
            assertThat(result.receiverZipCode()).isEqualTo("12345");
            assertThat(result.receiverAddress()).isEqualTo("서울시 강남구 테헤란로 1");
            assertThat(result.receiverAddressDetail()).isEqualTo("101호");
            assertThat(result.deliveryRequest()).isEqualTo("부재시 문앞에 놓아주세요");
        }

        @Test
        @DisplayName("toDetailWithHistory: 히스토리 변환 포함")
        void toDetailWithHistoryIncludesHistories() {
            OrderHistoryResult history =
                    new OrderHistoryResult(
                            1L,
                            "READY",
                            "CONFIRMED",
                            "system",
                            "발주확인",
                            Instant.parse("2026-02-18T11:00:00Z"));
            ProductOrderDetailResult detail =
                    new ProductOrderDetailResult(
                            orderInfo(),
                            productOrderInfo(),
                            paymentInfo(),
                            receiverInfo(),
                            new DeliveryInfo("CONFIRMED", null),
                            noneCancel(),
                            noneClaim(),
                            List.of(),
                            List.of(),
                            List.of(history));

            LegacyOrderDetailWithHistoryResult result =
                    assembler.toDetailWithHistory(
                            detail,
                            mapping,
                            (com.ryuqq.marketplace.domain.shipment.aggregate.Shipment) null);

            assertThat(result.order().orderStatus()).isEqualTo("DELIVERY_COMPLETED");
            assertThat(result.histories()).hasSize(1);
            assertThat(result.histories().getFirst().orderId()).isEqualTo(5001L);
            assertThat(result.histories().getFirst().orderStatus()).isEqualTo("CONFIRMED");
        }

        @Test
        @DisplayName("optionValues: externalOptionName 기반 변환")
        void optionValuesFromExternalOptionName() {
            ProductOrderDetailResult detail = detailWith("CONFIRMED", noneCancel(), noneClaim());

            LegacyOrderDetailResult result =
                    assembler.toDetailResult(
                            detail,
                            mapping,
                            (com.ryuqq.marketplace.domain.shipment.aggregate.Shipment) null);

            assertThat(result.optionValues()).containsExactly("블랙 / L");
        }

        @Test
        @DisplayName("null 필드 → 빈 문자열")
        void nullFieldsToEmptyStrings() {
            ProductOrderInfo productInfo =
                    new ProductOrderInfo(
                            1001L,
                            "ORD-001-001",
                            100L,
                            1L,
                            1L,
                            null,
                            200L,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            10000,
                            10000,
                            2,
                            20000,
                            0,
                            20000);

            ProductOrderDetailResult detail =
                    new ProductOrderDetailResult(
                            orderInfo(),
                            productInfo,
                            paymentInfo(),
                            receiverInfo(),
                            new DeliveryInfo("CONFIRMED", null),
                            noneCancel(),
                            noneClaim(),
                            List.of(),
                            List.of(),
                            List.of());

            LegacyOrderDetailResult result =
                    assembler.toDetailResult(
                            detail,
                            mapping,
                            (com.ryuqq.marketplace.domain.shipment.aggregate.Shipment) null);

            assertThat(result.productGroupName()).isEmpty();
            assertThat(result.brandName()).isEmpty();
            assertThat(result.mainImageUrl()).isEmpty();
            assertThat(result.optionValues()).isEmpty();
        }
    }

    // ==================== 테스트 헬퍼 ====================

    private ProductOrderDetailResult detailWith(
            String orderItemStatus, CancelSummary cancel, ClaimSummary claim) {
        return new ProductOrderDetailResult(
                orderInfo(),
                productOrderInfo(),
                paymentInfo(),
                receiverInfo(),
                new DeliveryInfo(orderItemStatus, null),
                cancel,
                claim,
                List.of(),
                List.of(),
                List.of());
    }

    private OrderInfo orderInfo() {
        return new OrderInfo(
                "01900000-0000-7000-8000-000000000001",
                "ORD-20260218-0001",
                1L,
                10L,
                "SETOF",
                "세토프",
                "EXT-ORD-001",
                Instant.parse("2026-02-18T10:00:00Z"),
                "홍길동",
                "buyer@example.com",
                "010-1234-5678",
                Instant.parse("2026-02-18T10:06:00Z"),
                Instant.parse("2026-02-18T10:06:00Z"));
    }

    private ProductOrderInfo productOrderInfo() {
        return new ProductOrderInfo(
                1001L,
                "ORD-20260218-0001-001",
                100L,
                1L,
                1L,
                null,
                200L,
                "SKU-001",
                "테스트 상품그룹",
                "테스트 브랜드",
                "테스트 셀러",
                "https://example.com/images/main.jpg",
                "EXT-PROD-001",
                "EXT-OPT-001",
                "테스트 상품명",
                "블랙 / L",
                "https://example.com/images/product.jpg",
                10000,
                10000,
                2,
                20000,
                0,
                20000);
    }

    private PaymentInfo paymentInfo() {
        return new PaymentInfo(
                "01900000-0000-7000-8000-000000000002",
                "PAY-20260218-0001",
                "PAID",
                "CARD",
                "PG-TXN-001",
                20000,
                Instant.parse("2026-02-18T10:05:00Z"),
                null);
    }

    private ReceiverInfo receiverInfo() {
        return new ReceiverInfo(
                "김수령", "010-9876-5432", "12345", "서울시 강남구 테헤란로 1", "101호", "부재시 문앞에 놓아주세요");
    }

    private CancelSummary noneCancel() {
        return CancelSummary.none(2);
    }

    private ClaimSummary noneClaim() {
        return ClaimSummary.none(2);
    }

    private CancelSummary activeCancel(String status) {
        return new CancelSummary(
                true,
                0,
                2,
                new LatestCancel("1", "C-001", status, 1, Instant.parse("2026-02-19T10:00:00Z")));
    }

    private CancelSummary completedCancel() {
        return new CancelSummary(
                false,
                1,
                1,
                new LatestCancel(
                        "1", "C-001", "COMPLETED", 1, Instant.parse("2026-02-19T10:00:00Z")));
    }

    private ClaimSummary activeClaim(String status) {
        return new ClaimSummary(
                true,
                1,
                1,
                1,
                new LatestClaim(
                        "1", "CL-001", "REFUND", status, 1, Instant.parse("2026-02-20T10:00:00Z")));
    }
}
