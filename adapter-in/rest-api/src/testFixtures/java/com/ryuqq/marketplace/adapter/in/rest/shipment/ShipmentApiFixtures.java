package com.ryuqq.marketplace.adapter.in.rest.shipment;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ConfirmShipmentBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest.ShipBatchItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipSingleApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipmentSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse.ShipmentMethodApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentSummaryApiResponse;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult.ShipmentMethodResult;
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
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ShipmentApiFixtures {

    private ShipmentApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_SHIPMENT_ID = "SHIP-001";
    public static final String DEFAULT_SHIPMENT_NUMBER = "SN-20250101-001";
    public static final String DEFAULT_ORDER_ID = "ORDER-001";
    public static final String DEFAULT_ORDER_NUMBER = "ON-20250101-001";
    public static final String DEFAULT_STATUS = "READY";
    public static final String DEFAULT_TRACKING_NUMBER = "1234567890";
    public static final String DEFAULT_COURIER_CODE = "CJ";
    public static final String DEFAULT_COURIER_NAME = "CJ대한통운";
    public static final String DEFAULT_SHIPMENT_METHOD_TYPE = "PARCEL";

    // ===== ConfirmShipmentBatchApiRequest =====

    public static ConfirmShipmentBatchApiRequest confirmBatchRequest() {
        return new ConfirmShipmentBatchApiRequest(List.of("SHIP-001", "SHIP-002", "SHIP-003"));
    }

    public static ConfirmShipmentBatchApiRequest confirmBatchRequest(List<String> shipmentIds) {
        return new ConfirmShipmentBatchApiRequest(shipmentIds);
    }

    // ===== ShipBatchApiRequest =====

    public static ShipBatchApiRequest shipBatchRequest() {
        List<ShipBatchItemApiRequest> items =
                List.of(shipBatchItemRequest("SHIP-001"), shipBatchItemRequest("SHIP-002"));
        return new ShipBatchApiRequest(items);
    }

    public static ShipBatchItemApiRequest shipBatchItemRequest(String shipmentId) {
        return new ShipBatchItemApiRequest(
                shipmentId,
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                DEFAULT_SHIPMENT_METHOD_TYPE);
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
        return new ShipmentSearchApiRequest(null, null, null, null, null, null, null, null);
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
                statuses, searchField, searchWord, null, sortKey, sortDirection, page, size);
    }

    public static ShipmentSearchApiRequest searchRequestWithStatuses(List<String> statuses) {
        return new ShipmentSearchApiRequest(statuses, null, null, null, "createdAt", "DESC", 0, 20);
    }

    // ===== ShipmentSummaryResult (Application) =====

    public static ShipmentSummaryResult summaryResult() {
        return new ShipmentSummaryResult(10, 5, 30, 15, 100, 2, 3);
    }

    // ===== ShipmentListResult (Application) =====

    public static ShipmentListResult listResult(String shipmentId) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new ShipmentListResult(
                shipmentId,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_NAME,
                now,
                null,
                now);
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
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        ShipmentMethodResult method =
                new ShipmentMethodResult(
                        DEFAULT_SHIPMENT_METHOD_TYPE, DEFAULT_COURIER_CODE, DEFAULT_COURIER_NAME);
        return new ShipmentDetailResult(
                shipmentId,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                method,
                DEFAULT_TRACKING_NUMBER,
                now,
                now,
                null,
                now,
                now);
    }

    public static ShipmentDetailResult detailResultWithoutMethod(String shipmentId) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new ShipmentDetailResult(
                shipmentId,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                null,
                null,
                now,
                null,
                null,
                now,
                now);
    }

    // ===== BatchProcessingResult (Application) =====

    public static BatchProcessingResult<String> batchSuccessResult(List<String> ids) {
        List<BatchItemResult<String>> items = ids.stream().map(BatchItemResult::success).toList();
        return BatchProcessingResult.from(items);
    }

    public static BatchProcessingResult<String> batchMixedResult() {
        List<BatchItemResult<String>> items =
                List.of(
                        BatchItemResult.success("SHIP-001"),
                        BatchItemResult.failure(
                                "SHIP-002", "ALREADY_CONFIRMED", "이미 발주 확인된 배송입니다."),
                        BatchItemResult.success("SHIP-003"));
        return BatchProcessingResult.from(items);
    }

    // ===== ShipmentSummaryApiResponse =====

    public static ShipmentSummaryApiResponse summaryApiResponse() {
        return new ShipmentSummaryApiResponse(10, 5, 30, 15, 100, 2, 3);
    }

    // ===== ShipmentListApiResponse =====

    public static ShipmentListApiResponse listApiResponse(String shipmentId) {
        return new ShipmentListApiResponse(
                shipmentId,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_NAME,
                "2025-01-23T10:30:00+09:00",
                null,
                "2025-01-23T10:30:00+09:00");
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
        ShipmentMethodApiResponse method =
                new ShipmentMethodApiResponse(
                        DEFAULT_SHIPMENT_METHOD_TYPE, DEFAULT_COURIER_CODE, DEFAULT_COURIER_NAME);
        return new ShipmentDetailApiResponse(
                shipmentId,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                method,
                DEFAULT_TRACKING_NUMBER,
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00",
                null,
                "2025-01-23T10:30:00+09:00",
                "2025-01-23T10:30:00+09:00");
    }

    // ===== BatchResultApiResponse =====

    public static BatchResultApiResponse batchResultApiResponse() {
        List<BatchResultItemApiResponse> items =
                List.of(
                        new BatchResultItemApiResponse("SHIP-001", true, null, null),
                        new BatchResultItemApiResponse(
                                "SHIP-002", false, "ALREADY_CONFIRMED", "이미 발주 확인된 배송입니다."),
                        new BatchResultItemApiResponse("SHIP-003", true, null, null));
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
