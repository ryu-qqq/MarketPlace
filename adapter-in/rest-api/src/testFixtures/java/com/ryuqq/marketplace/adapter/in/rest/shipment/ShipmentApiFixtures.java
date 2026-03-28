package com.ryuqq.marketplace.adapter.in.rest.shipment;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ConfirmShipmentBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest.ShipBatchItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest.ShipMethodRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipSingleApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipmentSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse.PaymentInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.OrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ProductOrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ReceiverInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ShipmentInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentSummaryApiResponse;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Shipment API 테스트 Fixtures.
 *
 * <p>Shipment REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 */
public final class ShipmentApiFixtures {

    private ShipmentApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_SHIPMENT_ID = "SHIP-001";
    public static final String DEFAULT_SHIPMENT_NUMBER = "SN-20250101-001";
    public static final long DEFAULT_ORDER_ITEM_ID = 1001L;
    public static final String DEFAULT_ORDER_ITEM_ID_STR = String.valueOf(DEFAULT_ORDER_ITEM_ID);
    public static final String DEFAULT_ORDER_ID = "ORD-001";
    public static final String DEFAULT_ORDER_NUMBER = "ON-20250101-001";
    public static final String DEFAULT_STATUS = "READY";
    public static final String DEFAULT_TRACKING_NUMBER = "1234567890";
    public static final String DEFAULT_COURIER_CODE = "CJ";
    public static final String DEFAULT_COURIER_NAME = "CJ대한통운";
    public static final String DEFAULT_SHIPMENT_METHOD_TYPE = "COURIER";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23 10:30:00";

    // ===== ConfirmShipmentBatchApiRequest =====

    public static ConfirmShipmentBatchApiRequest confirmBatchRequest() {
        return new ConfirmShipmentBatchApiRequest(
                List.of(
                        "01940001-0000-7000-8000-000000000001",
                        "01940001-0000-7000-8000-000000000002",
                        "01940001-0000-7000-8000-000000000003"));
    }

    public static ConfirmShipmentBatchApiRequest confirmBatchRequest(List<String> orderItemIds) {
        return new ConfirmShipmentBatchApiRequest(orderItemIds);
    }

    // ===== ShipBatchApiRequest =====

    public static ShipBatchApiRequest shipBatchRequest() {
        List<ShipBatchItemApiRequest> items =
                List.of(
                        shipBatchItemRequest("ORD-20260324-0001"),
                        shipBatchItemRequest("ORD-20260324-0002"));
        return new ShipBatchApiRequest(items, null);
    }

    public static ShipBatchItemApiRequest shipBatchItemRequest(String orderNumber) {
        return new ShipBatchItemApiRequest(
                orderNumber,
                new ShipMethodRequest(DEFAULT_SHIPMENT_METHOD_TYPE, DEFAULT_COURIER_CODE),
                DEFAULT_TRACKING_NUMBER);
    }

    // ===== ShipSingleApiRequest =====

    public static ShipSingleApiRequest shipSingleRequest() {
        return new ShipSingleApiRequest(
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                DEFAULT_SHIPMENT_METHOD_TYPE);
    }

    // ===== ShipmentSearchApiRequest =====

    public static ShipmentSearchApiRequest searchRequest() {
        return new ShipmentSearchApiRequest(
                null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static ShipmentSearchApiRequest searchRequest(
            List<String> statuses,
            String searchField,
            String searchWord,
            String sortKey,
            String sortDirection,
            Integer page,
            Integer size) {
        return new ShipmentSearchApiRequest(
                null,
                null,
                null,
                statuses,
                null,
                null,
                searchField,
                searchWord,
                sortKey,
                sortDirection,
                page,
                size);
    }

    public static ShipmentSearchApiRequest searchRequestWithStatuses(List<String> statuses) {
        return new ShipmentSearchApiRequest(
                null, null, null, statuses, null, null, null, null, "createdAt", "DESC", 0, 20);
    }

    // ===== ShipmentSummaryResult (Application) =====

    public static ShipmentSummaryResult summaryResult() {
        return new ShipmentSummaryResult(10, 5, 30, 15, 100, 2, 3);
    }

    // ===== ShipmentListResult (Application) =====

    public static ShipmentListResult listResult(String shipmentId) {
        return new ShipmentListResult(
                new ShipmentListResult.ShipmentInfo(
                        shipmentId,
                        DEFAULT_SHIPMENT_NUMBER,
                        DEFAULT_STATUS,
                        DEFAULT_TRACKING_NUMBER,
                        DEFAULT_COURIER_CODE,
                        DEFAULT_COURIER_NAME,
                        DEFAULT_INSTANT,
                        DEFAULT_INSTANT,
                        null,
                        DEFAULT_INSTANT),
                new ShipmentListResult.OrderInfo(
                        DEFAULT_ORDER_ID,
                        DEFAULT_ORDER_NUMBER,
                        "COMPLETED",
                        1L,
                        1L,
                        "SHOP-001",
                        "테스트샵",
                        "EXT-001",
                        DEFAULT_INSTANT,
                        "홍길동",
                        "buyer@test.com",
                        "010-1234-5678",
                        DEFAULT_INSTANT,
                        DEFAULT_INSTANT),
                new ShipmentListResult.ProductOrderInfo(
                        DEFAULT_ORDER_ITEM_ID,
                        "ORD-20250101-001-001",
                        100L,
                        200L,
                        "SKU-001",
                        "테스트상품",
                        "테스트브랜드",
                        "테스트셀러",
                        "https://img.test.com/main.jpg",
                        "EXT-P-001",
                        "EXT-O-001",
                        "외부상품명",
                        "외부옵션명",
                        "https://img.test.com/ext.jpg",
                        10000,
                        1,
                        10000,
                        0,
                        10000),
                new ShipmentListResult.ReceiverInfo(
                        "김수령", "010-9876-5432", "12345", "서울시 강남구", "101동 202호", "문 앞에 놓아주세요"));
    }

    public static List<ShipmentListResult> listResults(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> listResult("SHIP-00" + i)).toList();
    }

    public static ShipmentPageResult pageResult(int count, int page, int size) {
        List<ShipmentListResult> results = listResults(count);
        PageMeta pageMeta = PageMeta.of(page, size, count);
        return new ShipmentPageResult(results, pageMeta);
    }

    public static ShipmentPageResult emptyPageResult() {
        PageMeta pageMeta = PageMeta.of(0, 20, 0);
        return new ShipmentPageResult(List.of(), pageMeta);
    }

    // ===== ShipmentDetailResult (Application) =====

    public static ShipmentDetailResult detailResult(String shipmentId) {
        return new ShipmentDetailResult(
                new ShipmentListResult.ShipmentInfo(
                        shipmentId,
                        DEFAULT_SHIPMENT_NUMBER,
                        DEFAULT_STATUS,
                        DEFAULT_TRACKING_NUMBER,
                        DEFAULT_COURIER_CODE,
                        DEFAULT_COURIER_NAME,
                        DEFAULT_INSTANT,
                        DEFAULT_INSTANT,
                        null,
                        DEFAULT_INSTANT),
                new ShipmentListResult.OrderInfo(
                        DEFAULT_ORDER_ID,
                        DEFAULT_ORDER_NUMBER,
                        "COMPLETED",
                        1L,
                        1L,
                        "SHOP-001",
                        "테스트샵",
                        "EXT-001",
                        DEFAULT_INSTANT,
                        "홍길동",
                        "buyer@test.com",
                        "010-1234-5678",
                        DEFAULT_INSTANT,
                        DEFAULT_INSTANT),
                new ShipmentListResult.ProductOrderInfo(
                        DEFAULT_ORDER_ITEM_ID,
                        "ORD-20250101-001-001",
                        100L,
                        200L,
                        "SKU-001",
                        "테스트상품",
                        "테스트브랜드",
                        "테스트셀러",
                        "https://img.test.com/main.jpg",
                        "EXT-P-001",
                        "EXT-O-001",
                        "외부상품명",
                        "외부옵션명",
                        "https://img.test.com/ext.jpg",
                        10000,
                        1,
                        10000,
                        0,
                        10000),
                new ShipmentListResult.ReceiverInfo(
                        "김수령", "010-9876-5432", "12345", "서울시 강남구", "101동 202호", "문 앞에 놓아주세요"),
                new ShipmentDetailResult.PaymentInfo(
                        "PAY-001",
                        "PN-20250101-001",
                        "COMPLETED",
                        "CARD",
                        "PG-001",
                        10000,
                        DEFAULT_INSTANT,
                        null));
    }

    public static ShipmentDetailResult detailResultWithoutPayment(String shipmentId) {
        return new ShipmentDetailResult(
                new ShipmentListResult.ShipmentInfo(
                        shipmentId,
                        DEFAULT_SHIPMENT_NUMBER,
                        DEFAULT_STATUS,
                        null,
                        null,
                        null,
                        DEFAULT_INSTANT,
                        null,
                        null,
                        DEFAULT_INSTANT),
                new ShipmentListResult.OrderInfo(
                        DEFAULT_ORDER_ID,
                        DEFAULT_ORDER_NUMBER,
                        "COMPLETED",
                        1L,
                        1L,
                        "SHOP-001",
                        "테스트샵",
                        "EXT-001",
                        DEFAULT_INSTANT,
                        "홍길동",
                        "buyer@test.com",
                        "010-1234-5678",
                        DEFAULT_INSTANT,
                        DEFAULT_INSTANT),
                new ShipmentListResult.ProductOrderInfo(
                        DEFAULT_ORDER_ITEM_ID,
                        "ORD-20250101-001-001",
                        100L,
                        200L,
                        "SKU-001",
                        "테스트상품",
                        "테스트브랜드",
                        "테스트셀러",
                        "https://img.test.com/main.jpg",
                        "EXT-P-001",
                        "EXT-O-001",
                        "외부상품명",
                        "외부옵션명",
                        "https://img.test.com/ext.jpg",
                        10000,
                        1,
                        10000,
                        0,
                        10000),
                new ShipmentListResult.ReceiverInfo(
                        "김수령", "010-9876-5432", "12345", "서울시 강남구", "101동 202호", "문 앞에 놓아주세요"),
                null);
    }

    // ===== BatchProcessingResult (Application) =====

    public static BatchProcessingResult<String> batchSuccessResult(List<String> ids) {
        List<BatchItemResult<String>> items = ids.stream().map(BatchItemResult::success).toList();
        return BatchProcessingResult.from(items);
    }

    public static BatchProcessingResult<String> batchMixedResult() {
        List<BatchItemResult<String>> items =
                List.of(
                        BatchItemResult.success("1001"),
                        BatchItemResult.failure("1002", "ALREADY_CONFIRMED", "이미 발주 확인된 배송입니다."),
                        BatchItemResult.success("1003"));
        return BatchProcessingResult.from(items);
    }

    // ===== ShipmentSummaryApiResponse =====

    public static ShipmentSummaryApiResponse summaryApiResponse() {
        return new ShipmentSummaryApiResponse(10, 5, 30, 15, 100, 2, 3);
    }

    // ===== ShipmentListApiResponse =====

    public static ShipmentListApiResponse listApiResponse(String shipmentId) {
        return new ShipmentListApiResponse(
                new ShipmentInfoResponse(
                        shipmentId,
                        DEFAULT_SHIPMENT_NUMBER,
                        DEFAULT_STATUS,
                        DEFAULT_TRACKING_NUMBER,
                        DEFAULT_COURIER_CODE,
                        DEFAULT_COURIER_NAME,
                        DEFAULT_FORMATTED_TIME,
                        DEFAULT_FORMATTED_TIME,
                        null,
                        DEFAULT_FORMATTED_TIME),
                new OrderInfoResponse(
                        DEFAULT_ORDER_ID,
                        DEFAULT_ORDER_NUMBER,
                        "COMPLETED",
                        1L,
                        1L,
                        "SHOP-001",
                        "테스트샵",
                        "EXT-001",
                        DEFAULT_FORMATTED_TIME,
                        "홍길동",
                        "buyer@test.com",
                        "010-1234-5678",
                        DEFAULT_FORMATTED_TIME,
                        DEFAULT_FORMATTED_TIME),
                new ProductOrderInfoResponse(
                        DEFAULT_ORDER_ITEM_ID_STR,
                        100L,
                        200L,
                        "SKU-001",
                        "테스트상품",
                        "테스트브랜드",
                        "테스트셀러",
                        "https://img.test.com/main.jpg",
                        "EXT-P-001",
                        "EXT-O-001",
                        "외부상품명",
                        "외부옵션명",
                        "https://img.test.com/ext.jpg",
                        10000,
                        1,
                        10000,
                        0,
                        10000),
                new ReceiverInfoResponse(
                        "김수령", "010-9876-5432", "12345", "서울시 강남구", "101동 202호", "문 앞에 놓아주세요"));
    }

    public static List<ShipmentListApiResponse> listApiResponses(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> listApiResponse("SHIP-00" + i))
                .toList();
    }

    public static PageApiResponse<ShipmentListApiResponse> pageApiResponse(int count) {
        return PageApiResponse.of(listApiResponses(count), 0, 20, count);
    }

    // ===== ShipmentDetailApiResponse =====

    public static ShipmentDetailApiResponse detailApiResponse(String shipmentId) {
        return new ShipmentDetailApiResponse(
                new ShipmentInfoResponse(
                        shipmentId,
                        DEFAULT_SHIPMENT_NUMBER,
                        DEFAULT_STATUS,
                        DEFAULT_TRACKING_NUMBER,
                        DEFAULT_COURIER_CODE,
                        DEFAULT_COURIER_NAME,
                        DEFAULT_FORMATTED_TIME,
                        DEFAULT_FORMATTED_TIME,
                        null,
                        DEFAULT_FORMATTED_TIME),
                new OrderInfoResponse(
                        DEFAULT_ORDER_ID,
                        DEFAULT_ORDER_NUMBER,
                        "COMPLETED",
                        1L,
                        1L,
                        "SHOP-001",
                        "테스트샵",
                        "EXT-001",
                        DEFAULT_FORMATTED_TIME,
                        "홍길동",
                        "buyer@test.com",
                        "010-1234-5678",
                        DEFAULT_FORMATTED_TIME,
                        DEFAULT_FORMATTED_TIME),
                new ProductOrderInfoResponse(
                        DEFAULT_ORDER_ITEM_ID_STR,
                        100L,
                        200L,
                        "SKU-001",
                        "테스트상품",
                        "테스트브랜드",
                        "테스트셀러",
                        "https://img.test.com/main.jpg",
                        "EXT-P-001",
                        "EXT-O-001",
                        "외부상품명",
                        "외부옵션명",
                        "https://img.test.com/ext.jpg",
                        10000,
                        1,
                        10000,
                        0,
                        10000),
                new ReceiverInfoResponse(
                        "김수령", "010-9876-5432", "12345", "서울시 강남구", "101동 202호", "문 앞에 놓아주세요"),
                new PaymentInfoResponse(
                        "PAY-001",
                        "PN-20250101-001",
                        "COMPLETED",
                        "CARD",
                        "PG-001",
                        10000,
                        DEFAULT_FORMATTED_TIME,
                        null));
    }

    // ===== BatchResultApiResponse =====

    public static BatchResultApiResponse batchResultApiResponse() {
        List<BatchResultItemApiResponse> items =
                List.of(
                        new BatchResultItemApiResponse("1001", true, null, null),
                        new BatchResultItemApiResponse(
                                "1002", false, "ALREADY_CONFIRMED", "이미 발주 확인된 배송입니다."),
                        new BatchResultItemApiResponse("1003", true, null, null));
        return new BatchResultApiResponse(3, 2, 1, items);
    }

    public static BatchResultApiResponse batchAllSuccessApiResponse(List<String> ids) {
        List<BatchResultItemApiResponse> items =
                ids.stream()
                        .map(id -> new BatchResultItemApiResponse(id, true, null, null))
                        .toList();
        return new BatchResultApiResponse(ids.size(), ids.size(), 0, items);
    }
}
