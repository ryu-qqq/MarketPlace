package com.ryuqq.marketplace.adapter.in.rest.exchange;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ApproveExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CollectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CompleteExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ConvertToRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ExchangeSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.HoldExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.PrepareExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RejectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RequestExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RequestExchangeBatchApiRequest.ExchangeRequestItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ShipExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ShipExchangeBatchApiRequest.ShipItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse.AmountAdjustmentApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse.CollectShipmentApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse.ExchangeClaimInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse.ExchangeOptionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult.AmountAdjustmentResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult.ExchangeOptionResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeListResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * Exchange API 테스트 Fixtures.
 *
 * <p>Exchange REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExchangeApiFixtures {

    private ExchangeApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_EXCHANGE_CLAIM_ID = "01940001-0000-7000-9000-000000000001";
    public static final String DEFAULT_CLAIM_NUMBER = "EXC-20260101-0001";
    public static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    public static final String DEFAULT_LINKED_ORDER_ID = "01940001-0000-7000-8000-000000000099";
    public static final String DEFAULT_EXCHANGE_STATUS = "REQUESTED";
    public static final String DEFAULT_REASON_TYPE = "SIZE_CHANGE";
    public static final String DEFAULT_REASON_DETAIL = "사이즈를 변경하고 싶습니다";
    public static final String DEFAULT_TARGET_SKU_CODE = "SKU-TARGET-001";
    public static final String DEFAULT_ORIGINAL_SKU_CODE = "SKU-ORIGIN-001";
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final long DEFAULT_ORIGINAL_PRODUCT_ID = 100L;
    public static final long DEFAULT_TARGET_PRODUCT_GROUP_ID = 200L;
    public static final long DEFAULT_TARGET_PRODUCT_ID = 201L;
    public static final int DEFAULT_EXCHANGE_QTY = 1;
    public static final int DEFAULT_TARGET_QUANTITY = 1;
    public static final String DEFAULT_REQUESTED_BY = "seller@test.com";
    public static final String DEFAULT_PROCESSED_BY = "admin@test.com";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2026-01-01T00:00:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2026-01-01 09:00:00";
    public static final String DEFAULT_DELIVERY_COMPANY = "CJ대한통운";
    public static final String DEFAULT_TRACKING_NUMBER = "1234567890";
    public static final String DEFAULT_HISTORY_ID = "HIST-001";

    // ===== Request Fixtures =====

    public static RequestExchangeBatchApiRequest requestBatchRequest() {
        List<ExchangeRequestItemApiRequest> items =
                List.of(
                        new ExchangeRequestItemApiRequest(
                                DEFAULT_ORDER_ITEM_ID,
                                DEFAULT_EXCHANGE_QTY,
                                DEFAULT_REASON_TYPE,
                                DEFAULT_REASON_DETAIL,
                                DEFAULT_ORIGINAL_PRODUCT_ID,
                                DEFAULT_ORIGINAL_SKU_CODE,
                                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                                DEFAULT_TARGET_PRODUCT_ID,
                                DEFAULT_TARGET_SKU_CODE,
                                DEFAULT_TARGET_QUANTITY));
        return new RequestExchangeBatchApiRequest(items);
    }

    public static ApproveExchangeBatchApiRequest approveBatchRequest() {
        return new ApproveExchangeBatchApiRequest(List.of(DEFAULT_EXCHANGE_CLAIM_ID));
    }

    public static ApproveExchangeBatchApiRequest approveBatchRequest(
            List<String> exchangeClaimIds) {
        return new ApproveExchangeBatchApiRequest(exchangeClaimIds);
    }

    public static CollectExchangeBatchApiRequest collectBatchRequest() {
        return new CollectExchangeBatchApiRequest(List.of(DEFAULT_EXCHANGE_CLAIM_ID));
    }

    public static PrepareExchangeBatchApiRequest prepareBatchRequest() {
        return new PrepareExchangeBatchApiRequest(List.of(DEFAULT_EXCHANGE_CLAIM_ID));
    }

    public static RejectExchangeBatchApiRequest rejectBatchRequest() {
        return new RejectExchangeBatchApiRequest(List.of(DEFAULT_EXCHANGE_CLAIM_ID));
    }

    public static ShipExchangeBatchApiRequest shipBatchRequest() {
        List<ShipItemApiRequest> items =
                List.of(
                        new ShipItemApiRequest(
                                DEFAULT_EXCHANGE_CLAIM_ID,
                                DEFAULT_LINKED_ORDER_ID,
                                DEFAULT_DELIVERY_COMPANY,
                                DEFAULT_TRACKING_NUMBER));
        return new ShipExchangeBatchApiRequest(items);
    }

    public static CompleteExchangeBatchApiRequest completeBatchRequest() {
        return new CompleteExchangeBatchApiRequest(List.of(DEFAULT_EXCHANGE_CLAIM_ID));
    }

    public static ConvertToRefundBatchApiRequest convertToRefundBatchRequest() {
        return new ConvertToRefundBatchApiRequest(List.of(DEFAULT_EXCHANGE_CLAIM_ID));
    }

    public static HoldExchangeBatchApiRequest holdBatchRequest() {
        return new HoldExchangeBatchApiRequest(
                List.of(DEFAULT_EXCHANGE_CLAIM_ID), true, "보류 처리 메모");
    }

    public static HoldExchangeBatchApiRequest releaseBatchRequest() {
        return new HoldExchangeBatchApiRequest(List.of(DEFAULT_EXCHANGE_CLAIM_ID), false, null);
    }

    public static AddClaimHistoryMemoApiRequest addMemoRequest() {
        return new AddClaimHistoryMemoApiRequest("수기 메모 내용입니다.");
    }

    public static ExchangeSearchApiRequest searchRequest() {
        return new ExchangeSearchApiRequest(
                null, null, null, null, null, null, null, null, null, null);
    }

    public static ExchangeSearchApiRequest searchRequest(
            List<String> statuses,
            String searchField,
            String searchWord,
            Integer page,
            Integer size) {
        return new ExchangeSearchApiRequest(
                statuses,
                searchField,
                searchWord,
                null,
                null,
                null,
                "REQUESTED_AT",
                "DESC",
                page,
                size);
    }

    // ===== Application Result Fixtures =====

    public static ExchangeSummaryResult summaryResult() {
        return new ExchangeSummaryResult(5, 3, 2, 4, 1, 10, 2, 1);
    }

    public static ExchangeListResult listResult() {
        return new ExchangeListResult(
                DEFAULT_EXCHANGE_CLAIM_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_EXCHANGE_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_LINKED_ORDER_ID,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                null);
    }

    public static ExchangePageResult pageResult(int count, int page, int size) {
        List<ExchangeListResult> results =
                java.util.stream.IntStream.rangeClosed(1, count)
                        .mapToObj(i -> listResult())
                        .toList();
        PageMeta pageMeta = PageMeta.of(page, size, count);
        return new ExchangePageResult(results, pageMeta);
    }

    public static ExchangePageResult emptyPageResult() {
        PageMeta pageMeta = PageMeta.of(0, 20, 0);
        return new ExchangePageResult(List.of(), pageMeta);
    }

    public static ClaimHistoryResult historyResult() {
        return new ClaimHistoryResult(
                DEFAULT_HISTORY_ID,
                "STATUS_CHANGE",
                "교환 요청",
                "교환이 요청되었습니다.",
                "SELLER",
                String.valueOf(DEFAULT_SELLER_ID),
                DEFAULT_REQUESTED_BY,
                DEFAULT_INSTANT);
    }

    public static ExchangeDetailResult detailResult() {
        return new ExchangeDetailResult(
                DEFAULT_EXCHANGE_CLAIM_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_EXCHANGE_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                new ExchangeOptionResult(
                        DEFAULT_ORIGINAL_PRODUCT_ID,
                        DEFAULT_ORIGINAL_SKU_CODE,
                        DEFAULT_TARGET_PRODUCT_GROUP_ID,
                        DEFAULT_TARGET_PRODUCT_ID,
                        DEFAULT_TARGET_SKU_CODE,
                        DEFAULT_TARGET_QUANTITY),
                new AmountAdjustmentResult(
                        30000, 35000, 5000, true, false, 3000, 3000, 6000, "SELLER"),
                new ExchangeDetailResult.CollectShipmentResult(
                        DEFAULT_DELIVERY_COMPANY, DEFAULT_TRACKING_NUMBER, "IN_TRANSIT"),
                DEFAULT_LINKED_ORDER_ID,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                List.of(historyResult()));
    }

    public static ExchangeDetailResult detailResultWithoutOption() {
        return new ExchangeDetailResult(
                DEFAULT_EXCHANGE_CLAIM_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_EXCHANGE_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                "",
                DEFAULT_REQUESTED_BY,
                "",
                DEFAULT_INSTANT,
                null,
                null,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                List.of());
    }

    // ===== BatchProcessingResult =====

    public static BatchProcessingResult<String> batchSuccessResult(List<String> ids) {
        List<BatchItemResult<String>> items = ids.stream().map(BatchItemResult::success).toList();
        return BatchProcessingResult.from(items);
    }

    public static BatchProcessingResult<String> batchMixedResult() {
        List<BatchItemResult<String>> items =
                List.of(
                        BatchItemResult.success(DEFAULT_EXCHANGE_CLAIM_ID),
                        BatchItemResult.failure(
                                "01940001-0000-7000-9000-000000000002",
                                "INVALID_STATUS",
                                "현재 상태에서 처리할 수 없습니다."),
                        BatchItemResult.success("01940001-0000-7000-9000-000000000003"));
        return BatchProcessingResult.from(items);
    }

    // ===== API Response Fixtures =====

    public static ExchangeSummaryApiResponse summaryApiResponse() {
        return new ExchangeSummaryApiResponse(5, 3, 2, 4, 1, 10, 2, 1);
    }

    public static ExchangeListApiResponse listApiResponse() {
        return new ExchangeListApiResponse(
                DEFAULT_EXCHANGE_CLAIM_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_EXCHANGE_STATUS,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_LINKED_ORDER_ID,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME,
                null);
    }

    public static List<ExchangeListApiResponse> listApiResponses(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> listApiResponse())
                .toList();
    }

    public static PageApiResponse<ExchangeListApiResponse> pageApiResponse(int count) {
        return PageApiResponse.of(listApiResponses(count), 0, 20, count);
    }

    public static ClaimHistoryApiResponse historyApiResponse() {
        return new ClaimHistoryApiResponse(
                DEFAULT_HISTORY_ID,
                "STATUS_CHANGE",
                "교환 요청",
                "교환이 요청되었습니다.",
                new ClaimHistoryApiResponse.ActorApiResponse(
                        "SELLER", String.valueOf(DEFAULT_SELLER_ID), DEFAULT_REQUESTED_BY),
                DEFAULT_FORMATTED_TIME);
    }

    public static ExchangeDetailApiResponse detailApiResponse() {
        ExchangeClaimInfoApiResponse claimInfo =
                new ExchangeClaimInfoApiResponse(
                        DEFAULT_EXCHANGE_CLAIM_ID,
                        DEFAULT_CLAIM_NUMBER,
                        DEFAULT_SELLER_ID,
                        DEFAULT_EXCHANGE_QTY,
                        DEFAULT_EXCHANGE_STATUS,
                        DEFAULT_REASON_TYPE,
                        DEFAULT_REASON_DETAIL,
                        new ExchangeOptionApiResponse(
                                DEFAULT_ORIGINAL_PRODUCT_ID,
                                DEFAULT_ORIGINAL_SKU_CODE,
                                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                                DEFAULT_TARGET_PRODUCT_ID,
                                DEFAULT_TARGET_SKU_CODE,
                                DEFAULT_TARGET_QUANTITY),
                        new AmountAdjustmentApiResponse(
                                30000, 35000, 5000, true, false, 3000, 3000, 6000, "SELLER"),
                        new CollectShipmentApiResponse(
                                DEFAULT_DELIVERY_COMPANY, DEFAULT_TRACKING_NUMBER, "IN_TRANSIT"),
                        DEFAULT_LINKED_ORDER_ID,
                        DEFAULT_FORMATTED_TIME,
                        null);
        return new ExchangeDetailApiResponse(
                DEFAULT_ORDER_ITEM_ID,
                null,
                claimInfo,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME,
                List.of(historyApiResponse()));
    }

    public static BatchResultApiResponse batchResultApiResponse() {
        List<BatchResultItemApiResponse> items =
                List.of(
                        new BatchResultItemApiResponse(DEFAULT_EXCHANGE_CLAIM_ID, true, null, null),
                        new BatchResultItemApiResponse(
                                "01940001-0000-7000-9000-000000000002",
                                false,
                                "INVALID_STATUS",
                                "현재 상태에서 처리할 수 없습니다."),
                        new BatchResultItemApiResponse(
                                "01940001-0000-7000-9000-000000000003", true, null, null));
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
