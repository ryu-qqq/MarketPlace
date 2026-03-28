package com.ryuqq.marketplace.application.shipment;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult.PaymentInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.OrderInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ShipmentInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * Shipment Application Query 테스트 Fixtures.
 *
 * <p>Shipment 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ShipmentQueryFixtures {

    private ShipmentQueryFixtures() {}

    private static final String DEFAULT_SHIPMENT_ID = "01944b2a-1234-7fff-8888-abcdef012345";
    private static final String DEFAULT_SHIPMENT_NUMBER = "SHP-20260218-0001";
    private static final Long DEFAULT_ORDER_ITEM_ID = 1001L;
    private static final String DEFAULT_ORDER_ID = "01900000-0000-7000-8000-000000000001";
    private static final Instant FIXED_NOW = Instant.parse("2026-02-18T10:00:00Z");

    // ===== ShipmentSearchParams Fixtures =====

    public static ShipmentSearchParams searchParams() {
        CommonSearchParams common =
                new CommonSearchParams(false, null, null, "CREATED_AT", "DESC", 0, 20);
        return new ShipmentSearchParams(null, null, null, null, null, null, common);
    }

    public static ShipmentSearchParams searchParams(int page, int size) {
        CommonSearchParams common =
                new CommonSearchParams(false, null, null, "CREATED_AT", "DESC", page, size);
        return new ShipmentSearchParams(null, null, null, null, null, null, common);
    }

    public static ShipmentSearchParams searchParamsByStatus(String status) {
        CommonSearchParams common =
                new CommonSearchParams(false, null, null, "CREATED_AT", "DESC", 0, 20);
        return new ShipmentSearchParams(List.of(status), null, null, null, null, null, common);
    }

    public static ShipmentSearchParams searchParamsBySeller(Long sellerId) {
        CommonSearchParams common =
                new CommonSearchParams(false, null, null, "CREATED_AT", "DESC", 0, 20);
        return new ShipmentSearchParams(null, List.of(sellerId), null, null, null, null, common);
    }

    // ===== ShipmentInfo Fixtures =====

    public static ShipmentInfo shipmentInfo() {
        return new ShipmentInfo(
                DEFAULT_SHIPMENT_ID,
                DEFAULT_SHIPMENT_NUMBER,
                "PREPARING",
                null,
                null,
                null,
                FIXED_NOW,
                null,
                null,
                FIXED_NOW);
    }

    public static ShipmentInfo shippedShipmentInfo() {
        return new ShipmentInfo(
                DEFAULT_SHIPMENT_ID,
                DEFAULT_SHIPMENT_NUMBER,
                "SHIPPED",
                "1234567890",
                "CJ",
                "CJ대한통운",
                FIXED_NOW,
                FIXED_NOW.plusSeconds(3600),
                null,
                FIXED_NOW);
    }

    // ===== OrderInfo Fixtures =====

    public static OrderInfo orderInfo() {
        return new OrderInfo(
                DEFAULT_ORDER_ID,
                "ORD-20260218-0001",
                null,
                1L,
                10L,
                "NAVER",
                "네이버 스마트스토어",
                "EXT-ORDER-001",
                FIXED_NOW,
                "홍길동",
                "buyer@example.com",
                "010-1234-5678",
                FIXED_NOW,
                FIXED_NOW);
    }

    // ===== ProductOrderInfo Fixtures =====

    public static ProductOrderInfo productOrderInfo() {
        return new ProductOrderInfo(
                DEFAULT_ORDER_ITEM_ID,
                "ORD-20260218-0001-001",
                100L,
                200L,
                "SKU-TEST-0001",
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
                2,
                20000,
                0,
                20000);
    }

    // ===== ReceiverInfo Fixtures =====

    public static ReceiverInfo receiverInfo() {
        return new ReceiverInfo(
                "김수령", "010-9876-5432", "12345", "서울시 강남구 테헤란로 1", "101호", "부재시 문앞에 놓아주세요");
    }

    // ===== PaymentInfo Fixtures =====

    public static PaymentInfo paymentInfo() {
        return new PaymentInfo(
                "01900000-0000-7000-8000-000000000002",
                "PAY-20260218-0001",
                "PAID",
                "CARD",
                "PG-TXN-001",
                20000,
                FIXED_NOW.plusSeconds(300),
                null);
    }

    // ===== ShipmentListResult Fixtures =====

    public static ShipmentListResult shipmentListResult() {
        return new ShipmentListResult(
                shipmentInfo(), orderInfo(), productOrderInfo(), receiverInfo());
    }

    // ===== ShipmentDetailResult Fixtures =====

    public static ShipmentDetailResult shipmentDetailResult() {
        return new ShipmentDetailResult(
                shipmentInfo(), orderInfo(), productOrderInfo(), receiverInfo(), paymentInfo());
    }

    public static ShipmentDetailResult shipmentDetailResultWithoutPayment() {
        return new ShipmentDetailResult(
                shipmentInfo(), orderInfo(), productOrderInfo(), receiverInfo(), null);
    }

    // ===== ShipmentPageResult Fixtures =====

    public static ShipmentPageResult shipmentPageResult() {
        return shipmentPageResult(List.of(shipmentListResult()), 0, 20, 1L);
    }

    public static ShipmentPageResult shipmentPageResult(
            List<ShipmentListResult> items, int page, int size, long totalCount) {
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new ShipmentPageResult(items, pageMeta);
    }

    public static ShipmentPageResult emptyShipmentPageResult() {
        return shipmentPageResult(List.of(), 0, 20, 0L);
    }

    // ===== ShipmentSummaryResult Fixtures =====

    public static ShipmentSummaryResult shipmentSummaryResult() {
        return new ShipmentSummaryResult(5, 3, 10, 7, 2, 1, 0);
    }

    public static ShipmentSummaryResult emptyShipmentSummaryResult() {
        return new ShipmentSummaryResult(0, 0, 0, 0, 0, 0, 0);
    }
}
