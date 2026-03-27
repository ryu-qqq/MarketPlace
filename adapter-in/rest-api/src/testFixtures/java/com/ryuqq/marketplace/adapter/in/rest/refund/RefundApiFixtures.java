package com.ryuqq.marketplace.adapter.in.rest.refund;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse.ActorApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.ApproveRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.HoldRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RefundSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RejectRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RequestRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RequestRefundBatchApiRequest.RefundRequestItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundDetailApiResponse.CollectShipmentApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundDetailApiResponse.RefundClaimInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult.HoldInfoResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult.RefundInfoResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Refund API 테스트 Fixtures.
 *
 * <p>Refund REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * <p>V4 간극 패턴: orderId = 내부 orderItemId. legacyOrderId 제외.
 */
public final class RefundApiFixtures {

    private RefundApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_REFUND_CLAIM_ID = "01940001-0000-7000-8000-000000000001";
    public static final String DEFAULT_CLAIM_NUMBER = "RC-20250101-001";
    public static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000099";
    public static final String DEFAULT_REFUND_STATUS = "REQUESTED";
    public static final String DEFAULT_REASON_TYPE = "CHANGE_OF_MIND";
    public static final String DEFAULT_REASON_DETAIL = "단순 변심입니다";
    public static final String DEFAULT_REQUESTED_BY = "seller01";
    public static final String DEFAULT_PROCESSED_BY = "admin01";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23 10:30:00";

    // ===== RequestRefundBatchApiRequest =====

    public static RequestRefundBatchApiRequest requestBatchRequest() {
        List<RefundRequestItemApiRequest> items =
                List.of(
                        requestItemApiRequest("01940001-0000-7000-8000-000000000001"),
                        requestItemApiRequest("01940001-0000-7000-8000-000000000002"));
        return new RequestRefundBatchApiRequest(items);
    }

    public static RefundRequestItemApiRequest requestItemApiRequest(String orderId) {
        return new RefundRequestItemApiRequest(
                orderId, 1, DEFAULT_REASON_TYPE, DEFAULT_REASON_DETAIL);
    }

    // ===== ApproveRefundBatchApiRequest =====

    public static ApproveRefundBatchApiRequest approveBatchRequest() {
        return new ApproveRefundBatchApiRequest(
                List.of(
                        "01940001-0000-7000-8000-000000000001",
                        "01940001-0000-7000-8000-000000000002"));
    }

    public static ApproveRefundBatchApiRequest approveBatchRequest(List<String> refundClaimIds) {
        return new ApproveRefundBatchApiRequest(refundClaimIds);
    }

    // ===== RejectRefundBatchApiRequest =====

    public static RejectRefundBatchApiRequest rejectBatchRequest() {
        return new RejectRefundBatchApiRequest(
                List.of(
                        "01940001-0000-7000-8000-000000000001",
                        "01940001-0000-7000-8000-000000000002"));
    }

    public static RejectRefundBatchApiRequest rejectBatchRequest(List<String> refundClaimIds) {
        return new RejectRefundBatchApiRequest(refundClaimIds);
    }

    // ===== HoldRefundBatchApiRequest =====

    public static HoldRefundBatchApiRequest holdBatchRequest() {
        return new HoldRefundBatchApiRequest(
                List.of(
                        "01940001-0000-7000-8000-000000000001",
                        "01940001-0000-7000-8000-000000000002"),
                true,
                "추가 확인 필요");
    }

    public static HoldRefundBatchApiRequest releaseBatchRequest() {
        return new HoldRefundBatchApiRequest(
                List.of("01940001-0000-7000-8000-000000000001"), false, null);
    }

    // ===== AddClaimHistoryMemoApiRequest =====

    public static AddClaimHistoryMemoApiRequest addMemoRequest() {
        return new AddClaimHistoryMemoApiRequest("수기 메모 내용입니다.");
    }

    // ===== RefundSearchApiRequest =====

    public static RefundSearchApiRequest searchRequest() {
        return new RefundSearchApiRequest(
                null, null, null, null, null, null, null, null, null, null);
    }

    public static RefundSearchApiRequest searchRequest(
            List<String> statuses,
            String searchField,
            String searchWord,
            String sortKey,
            String sortDirection,
            Integer page,
            Integer size) {
        return new RefundSearchApiRequest(
                statuses,
                searchField,
                searchWord,
                null,
                null,
                null,
                sortKey,
                sortDirection,
                page,
                size);
    }

    public static RefundSearchApiRequest searchRequestWithStatuses(List<String> statuses) {
        return new RefundSearchApiRequest(
                statuses, null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    // ===== RefundSummaryResult (Application) =====

    public static RefundSummaryResult summaryResult() {
        return new RefundSummaryResult(10, 5, 3, 20, 2, 1);
    }

    // ===== RefundListResult (Application) =====

    public static RefundListResult listResult(String refundClaimId) {
        return new RefundListResult(
                refundClaimId,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                DEFAULT_REFUND_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                15000,
                15000,
                "CARD",
                DEFAULT_REQUESTED_BY,
                null,
                DEFAULT_INSTANT,
                null,
                null);
    }

    public static List<RefundListResult> listResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                listResult(
                                        "01940001-0000-7000-8000-0000000000"
                                                + String.format("%02d", i)))
                .toList();
    }

    public static RefundPageResult pageResult(int count, int page, int size) {
        List<RefundListResult> results = listResults(count);
        PageMeta pageMeta = PageMeta.of(page, size, count);
        return new RefundPageResult(results, pageMeta);
    }

    public static RefundPageResult emptyPageResult() {
        PageMeta pageMeta = PageMeta.of(0, 20, 0);
        return new RefundPageResult(List.of(), pageMeta);
    }

    // ===== ClaimHistoryResult (Application) =====

    public static ClaimHistoryResult claimHistoryResult() {
        return new ClaimHistoryResult(
                "HIST-001",
                "MEMO",
                "수기 메모",
                "수기 메모 내용입니다.",
                "SELLER",
                "1",
                "seller01",
                DEFAULT_INSTANT);
    }

    // ===== RefundDetailResult (Application) =====

    public static RefundDetailResult detailResult(String refundClaimId) {
        return new RefundDetailResult(
                refundClaimId,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                DEFAULT_REFUND_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                new RefundInfoResult(15000, 15000, 0, null, "CARD", DEFAULT_INSTANT),
                null,
                new RefundDetailResult.CollectShipmentResult(
                        new RefundDetailResult.CollectShipmentMethodResult(
                                "COURIER", "CJGLS", "CJ대한통운"),
                        new RefundDetailResult.CollectShipmentFeeInfoResult(0, "SELLER"),
                        "1234567890",
                        "IN_TRANSIT"),
                DEFAULT_REQUESTED_BY,
                null,
                DEFAULT_INSTANT,
                null,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                List.of(claimHistoryResult()));
    }

    public static RefundDetailResult detailResultWithHold(String refundClaimId) {
        return new RefundDetailResult(
                refundClaimId,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                "HOLD",
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                new HoldInfoResult("추가 확인 필요", DEFAULT_INSTANT),
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                List.of());
    }

    public static RefundDetailResult detailResultWithoutRefundInfo(String refundClaimId) {
        return new RefundDetailResult(
                refundClaimId,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                DEFAULT_REFUND_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                DEFAULT_INSTANT,
                null,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                List.of());
    }

    // ===== BatchProcessingResult (Application) =====

    public static BatchProcessingResult<String> batchSuccessResult(List<String> ids) {
        List<BatchItemResult<String>> items = ids.stream().map(BatchItemResult::success).toList();
        return BatchProcessingResult.from(items);
    }

    public static BatchProcessingResult<String> batchMixedResult() {
        List<BatchItemResult<String>> items =
                List.of(
                        BatchItemResult.success("01940001-0000-7000-8000-000000000001"),
                        BatchItemResult.failure(
                                "01940001-0000-7000-8000-000000000002",
                                "INVALID_STATUS",
                                "이미 처리된 환불 건입니다."),
                        BatchItemResult.success("01940001-0000-7000-8000-000000000003"));
        return BatchProcessingResult.from(items);
    }

    // ===== RefundSummaryApiResponse =====

    public static RefundSummaryApiResponse summaryApiResponse() {
        return new RefundSummaryApiResponse(10, 5, 3, 20, 2, 1);
    }

    // ===== RefundListApiResponse =====

    public static RefundListApiResponse listApiResponse(String refundClaimId) {
        return new RefundListApiResponse(
                refundClaimId,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                DEFAULT_REFUND_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                15000,
                15000,
                "CARD",
                DEFAULT_REQUESTED_BY,
                "",
                DEFAULT_FORMATTED_TIME,
                null,
                null);
    }

    public static List<RefundListApiResponse> listApiResponses(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                listApiResponse(
                                        "01940001-0000-7000-8000-0000000000"
                                                + String.format("%02d", i)))
                .toList();
    }

    public static PageApiResponse<RefundListApiResponse> pageApiResponse(int count) {
        return PageApiResponse.of(listApiResponses(count), 0, 20, count);
    }

    // ===== RefundDetailApiResponse =====

    public static RefundDetailApiResponse detailApiResponse(String refundClaimId) {
        RefundClaimInfoApiResponse claimInfo =
                new RefundClaimInfoApiResponse(
                        refundClaimId,
                        DEFAULT_CLAIM_NUMBER,
                        1,
                        DEFAULT_REFUND_STATUS,
                        DEFAULT_REASON_TYPE,
                        DEFAULT_REASON_DETAIL,
                        new ClaimListItemApiResponseV4.RefundInfoV4(
                                15000, 0, "", 15000, "CARD", DEFAULT_FORMATTED_TIME),
                        null,
                        new CollectShipmentApiResponse(
                                new RefundDetailApiResponse.CollectShipmentMethodApiResponse(
                                        "COURIER", "CJGLS", "CJ대한통운"),
                                new RefundDetailApiResponse.CollectShipmentFeeInfoApiResponse(
                                        0, "SELLER"),
                                "1234567890",
                                "IN_TRANSIT"),
                        DEFAULT_FORMATTED_TIME,
                        null);
        return new RefundDetailApiResponse(
                DEFAULT_ORDER_ITEM_ID,
                List.of(),
                claimInfo,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                "",
                null,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME,
                List.of(
                        new ClaimHistoryApiResponse(
                                "HIST-001",
                                "MEMO",
                                "수기 메모",
                                "수기 메모 내용입니다.",
                                new ActorApiResponse("SELLER", "1", "seller01"),
                                DEFAULT_FORMATTED_TIME)));
    }

    // ===== BatchResultApiResponse =====

    public static BatchResultApiResponse batchResultApiResponse() {
        List<BatchResultItemApiResponse> items =
                List.of(
                        new BatchResultItemApiResponse(
                                "01940001-0000-7000-8000-000000000001", true, null, null),
                        new BatchResultItemApiResponse(
                                "01940001-0000-7000-8000-000000000002",
                                false,
                                "INVALID_STATUS",
                                "이미 처리된 환불 건입니다."),
                        new BatchResultItemApiResponse(
                                "01940001-0000-7000-8000-000000000003", true, null, null));
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
