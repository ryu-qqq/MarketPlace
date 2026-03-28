package com.ryuqq.marketplace.adapter.in.rest.cancel;

import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.ApproveCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.CancelSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.RejectCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest.CancelReasonApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest.SellerCancelItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.CancelListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse.ActorApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult.RefundInfo;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Cancel API 테스트 Fixtures.
 *
 * <p>Cancel REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * <p>V4 간극 패턴: orderId = 내부 orderItemId. legacyOrderId 제외.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CancelApiFixtures {

    private CancelApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CANCEL_ID = "01940001-0000-7000-8000-000000000001";
    public static final String DEFAULT_CANCEL_NUMBER = "CAN-20250101-001";
    public static final long DEFAULT_ORDER_ITEM_ID = 1001L;
    public static final String DEFAULT_ORDER_ITEM_ID_STR = String.valueOf(DEFAULT_ORDER_ITEM_ID);
    public static final String DEFAULT_CANCEL_STATUS = "REQUESTED";
    public static final String DEFAULT_CANCEL_TYPE = "BUYER_CANCEL";
    public static final String DEFAULT_REASON_TYPE = "OUT_OF_STOCK";
    public static final String DEFAULT_REASON_DETAIL = "재고 소진으로 인한 취소";
    public static final String DEFAULT_REQUESTED_BY = "seller01";
    public static final String DEFAULT_PROCESSED_BY = "admin01";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-01-23 10:30:00";

    // ===== SellerCancelBatchApiRequest =====

    public static SellerCancelBatchApiRequest sellerCancelBatchRequest() {
        List<SellerCancelItemApiRequest> items =
                List.of(
                        sellerCancelItemRequest(DEFAULT_ORDER_ITEM_ID_STR),
                        sellerCancelItemRequest("1002"));
        return new SellerCancelBatchApiRequest(items, cancelReasonRequest(), DEFAULT_REASON_DETAIL);
    }

    public static SellerCancelItemApiRequest sellerCancelItemRequest(String orderId) {
        return new SellerCancelItemApiRequest(orderId, 1);
    }

    public static CancelReasonApiRequest cancelReasonRequest() {
        return new CancelReasonApiRequest(DEFAULT_REASON_TYPE, DEFAULT_REASON_DETAIL);
    }

    // ===== ApproveCancelBatchApiRequest =====

    public static ApproveCancelBatchApiRequest approveBatchRequest() {
        return new ApproveCancelBatchApiRequest(
                List.of(
                        "01940001-0000-7000-8000-000000000001",
                        "01940001-0000-7000-8000-000000000002"));
    }

    public static ApproveCancelBatchApiRequest approveBatchRequest(List<String> cancelIds) {
        return new ApproveCancelBatchApiRequest(cancelIds);
    }

    // ===== RejectCancelBatchApiRequest =====

    public static RejectCancelBatchApiRequest rejectBatchRequest() {
        return new RejectCancelBatchApiRequest(
                List.of(
                        "01940001-0000-7000-8000-000000000001",
                        "01940001-0000-7000-8000-000000000002"));
    }

    public static RejectCancelBatchApiRequest rejectBatchRequest(List<String> cancelIds) {
        return new RejectCancelBatchApiRequest(cancelIds);
    }

    // ===== AddClaimHistoryMemoApiRequest =====

    public static AddClaimHistoryMemoApiRequest addMemoRequest() {
        return new AddClaimHistoryMemoApiRequest("수기 메모 내용입니다.");
    }

    // ===== CancelSearchApiRequest =====

    public static CancelSearchApiRequest searchRequest() {
        return new CancelSearchApiRequest(
                null, null, null, null, null, null, null, null, null, null, null);
    }

    public static CancelSearchApiRequest searchRequest(
            List<String> statuses,
            String searchField,
            String searchWord,
            String sortKey,
            String sortDirection,
            Integer page,
            Integer size) {
        return new CancelSearchApiRequest(
                statuses,
                null,
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

    public static CancelSearchApiRequest searchRequestWithStatuses(List<String> statuses) {
        return new CancelSearchApiRequest(
                statuses, null, null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    // ===== CancelSummaryResult (Application) =====

    public static CancelSummaryResult summaryResult() {
        return new CancelSummaryResult(10, 5, 3, 20, 2);
    }

    // ===== CancelListResult (Application) =====

    public static CancelListResult listResult(String cancelId) {
        return new CancelListResult(
                cancelId,
                DEFAULT_CANCEL_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                DEFAULT_CANCEL_TYPE,
                DEFAULT_CANCEL_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                15000,
                "CARD",
                DEFAULT_REQUESTED_BY,
                null,
                DEFAULT_INSTANT,
                null,
                null);
    }

    public static List<CancelListResult> listResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                listResult(
                                        "01940001-0000-7000-8000-0000000000"
                                                + String.format("%02d", i)))
                .toList();
    }

    public static CancelPageResult pageResult(int count, int page, int size) {
        List<CancelListResult> results = listResults(count);
        PageMeta pageMeta = PageMeta.of(page, size, count);
        return new CancelPageResult(results, pageMeta);
    }

    public static CancelPageResult emptyPageResult() {
        PageMeta pageMeta = PageMeta.of(0, 20, 0);
        return new CancelPageResult(List.of(), pageMeta);
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

    // ===== CancelDetailResult (Application) =====

    public static CancelDetailResult detailResult(String cancelId) {
        return new CancelDetailResult(
                cancelId,
                DEFAULT_CANCEL_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                DEFAULT_CANCEL_TYPE,
                DEFAULT_CANCEL_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                new RefundInfo(15000, "CARD", "COMPLETED", DEFAULT_INSTANT, "PG-REF-001"),
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                List.of(claimHistoryResult()));
    }

    public static CancelDetailResult detailResultWithoutRefundInfo(String cancelId) {
        return new CancelDetailResult(
                cancelId,
                DEFAULT_CANCEL_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                1,
                DEFAULT_CANCEL_TYPE,
                DEFAULT_CANCEL_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
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
                                "이미 처리된 취소 건입니다."),
                        BatchItemResult.success("01940001-0000-7000-8000-000000000003"));
        return BatchProcessingResult.from(items);
    }

    // ===== CancelSummaryApiResponse =====

    public static CancelSummaryApiResponse summaryApiResponse() {
        return new CancelSummaryApiResponse(10, 5, 3, 20);
    }

    // ===== CancelListApiResponse =====

    public static CancelListApiResponse listApiResponse(String cancelId) {
        return new CancelListApiResponse(
                cancelId,
                DEFAULT_CANCEL_NUMBER,
                DEFAULT_ORDER_ITEM_ID_STR,
                1,
                DEFAULT_CANCEL_TYPE,
                DEFAULT_CANCEL_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                15000,
                "CARD",
                DEFAULT_REQUESTED_BY,
                "",
                DEFAULT_FORMATTED_TIME,
                null,
                null);
    }

    public static List<CancelListApiResponse> listApiResponses(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                listApiResponse(
                                        "01940001-0000-7000-8000-0000000000"
                                                + String.format("%02d", i)))
                .toList();
    }

    public static PageApiResponse<CancelListApiResponse> pageApiResponse(int count) {
        return PageApiResponse.of(listApiResponses(count), 0, 20, count);
    }

    // ===== CancelDetailApiResponse =====

    public static CancelDetailApiResponse detailApiResponse(String cancelId) {
        return new CancelDetailApiResponse(
                DEFAULT_ORDER_ITEM_ID_STR,
                null,
                new CancelListItemApiResponseV4.CancelInfoV4(
                        cancelId,
                        DEFAULT_CANCEL_NUMBER,
                        DEFAULT_CANCEL_TYPE,
                        DEFAULT_CANCEL_STATUS,
                        1,
                        DEFAULT_REASON_DETAIL,
                        new ClaimListItemApiResponseV4.RefundInfoV4(
                                15000, 0, "", 15000, "CARD", DEFAULT_FORMATTED_TIME),
                        DEFAULT_FORMATTED_TIME,
                        DEFAULT_FORMATTED_TIME),
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                DEFAULT_FORMATTED_TIME,
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

    public static CancelDetailApiResponse detailApiResponseWithoutRefund(String cancelId) {
        return new CancelDetailApiResponse(
                DEFAULT_ORDER_ITEM_ID_STR,
                null,
                new CancelListItemApiResponseV4.CancelInfoV4(
                        cancelId,
                        DEFAULT_CANCEL_NUMBER,
                        DEFAULT_CANCEL_TYPE,
                        DEFAULT_CANCEL_STATUS,
                        1,
                        DEFAULT_REASON_DETAIL,
                        null,
                        DEFAULT_FORMATTED_TIME,
                        null),
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                "",
                null,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME,
                List.of());
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
                                "이미 처리된 취소 건입니다."),
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
