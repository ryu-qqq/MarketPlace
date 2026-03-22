package com.ryuqq.marketplace.adapter.in.rest.cancel.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.ApproveCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.CancelSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.RejectCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest.SellerCancelItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Cancel API Mapper.
 *
 * <p>V4 간극 패턴:
 *
 * <ul>
 *   <li>orderId = 내부 orderItemId (프론트에겐 "주문 ID")
 *   <li>legacyOrderId 제외
 *   <li>Instant → ISO 8601 (+09:00) 문자열 변환
 *   <li>null → "" 기본값
 * </ul>
 */
@Component
public class CancelApiMapper {

    private static final DateTimeFormatter KST_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                    .withZone(ZoneId.of("Asia/Seoul"));

    // ==================== Command 변환 ====================

    public SellerCancelBatchCommand toSellerCancelBatchCommand(
            SellerCancelBatchApiRequest request, String requestedBy, long sellerId) {
        List<SellerCancelItem> items =
                request.items().stream().map(this::toSellerCancelItem).toList();
        return new SellerCancelBatchCommand(items, requestedBy, sellerId);
    }

    private SellerCancelItem toSellerCancelItem(SellerCancelItemApiRequest item) {
        return new SellerCancelItem(
                item.orderId(), // V4 간극: orderId = 내부 orderItemId
                item.cancelQty(),
                CancelReasonType.valueOf(item.reasonType()),
                item.reasonDetail());
    }

    public ApproveCancelBatchCommand toApproveCancelBatchCommand(
            ApproveCancelBatchApiRequest request, String processedBy, Long sellerId) {
        return new ApproveCancelBatchCommand(request.cancelIds(), processedBy, sellerId);
    }

    public RejectCancelBatchCommand toRejectCancelBatchCommand(
            RejectCancelBatchApiRequest request, String processedBy, Long sellerId) {
        return new RejectCancelBatchCommand(request.cancelIds(), processedBy, sellerId);
    }

    public AddClaimHistoryMemoCommand toAddMemoCommand(
            String cancelId,
            AddClaimHistoryMemoApiRequest request,
            long sellerId,
            String actorName) {
        return new AddClaimHistoryMemoCommand(
                ClaimType.CANCEL, cancelId, request.message(), String.valueOf(sellerId), actorName);
    }

    // ==================== Query 변환 ====================

    public CancelSearchParams toSearchParams(CancelSearchApiRequest request) {
        return new CancelSearchParams(
                request.statuses(),
                request.types(),
                request.searchField(),
                request.searchWord(),
                request.dateField(),
                request.startDate(),
                request.endDate(),
                request.sortKey(),
                request.sortDirection(),
                request.resolvedPage(),
                request.resolvedSize());
    }

    // ==================== V4 Response 변환 (프론트 중첩 구조) ====================

    public PageApiResponse<ClaimListItemApiResponseV4> toPageResponseV4(
            CancelPageResult result, ClaimOrderEnricher enricher) {
        List<String> orderItemIds =
                result.cancels().stream().map(CancelListResult::orderItemId).toList();
        ClaimOrderEnricher.OrderContext ctx = enricher.loadOrderContext(orderItemIds);

        List<ClaimListItemApiResponseV4> responses =
                result.cancels().stream().map(r -> toListResponseV4(r, enricher, ctx)).toList();
        return PageApiResponse.of(
                responses,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    private ClaimListItemApiResponseV4 toListResponseV4(
            CancelListResult r, ClaimOrderEnricher enricher, ClaimOrderEnricher.OrderContext ctx) {
        String itemId = r.orderItemId();
        return new ClaimListItemApiResponseV4(
                enricher.toOrderProductV4(itemId, ctx),
                enricher.toClaimInfoV4(
                        r.cancelId(),
                        r.cancelNumber(),
                        r.cancelStatus(),
                        r.cancelQty(),
                        r.reasonType(),
                        r.reasonDetail(),
                        r.refundAmount(),
                        r.refundAmount(),
                        r.refundMethod(),
                        "",
                        false,
                        r.requestedAt(),
                        r.requestedAt()),
                enricher.toBuyerInfoV4(itemId, ctx),
                enricher.toPaymentV4(itemId, ctx),
                enricher.toReceiverInfoV4(itemId, ctx),
                enricher.toExternalOrderInfoV4(itemId, ctx));
    }

    // ==================== Response 변환 ====================

    public CancelSummaryApiResponse toSummaryResponse(CancelSummaryResult result) {
        return new CancelSummaryApiResponse(
                result.requested(), result.approved(), result.rejected(), result.completed());
    }

    public PageApiResponse<CancelListApiResponse> toPageResponse(CancelPageResult result) {
        List<CancelListApiResponse> responses =
                result.cancels().stream().map(this::toListResponse).toList();
        return PageApiResponse.of(
                responses,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    public CancelListApiResponse toListResponse(CancelListResult result) {
        return new CancelListApiResponse(
                nullToEmpty(result.cancelId()),
                nullToEmpty(result.cancelNumber()),
                nullToEmpty(result.orderItemId()), // V4 간극: orderId = orderItemId
                result.cancelQty(),
                nullToEmpty(result.cancelType()),
                nullToEmpty(result.cancelStatus()),
                nullToEmpty(result.reasonType()),
                nullToEmpty(result.reasonDetail()),
                result.refundAmount() != null ? result.refundAmount() : 0,
                nullToEmpty(result.refundMethod()),
                nullToEmpty(result.requestedBy()),
                nullToEmpty(result.processedBy()),
                formatInstant(result.requestedAt()),
                formatInstant(result.processedAt()),
                formatInstant(result.completedAt()));
    }

    public CancelDetailApiResponse toDetailResponse(CancelDetailResult result) {
        CancelDetailApiResponse.RefundInfoApiResponse refundInfo = null;
        if (result.refundInfo() != null) {
            refundInfo =
                    new CancelDetailApiResponse.RefundInfoApiResponse(
                            result.refundInfo().refundAmount(),
                            nullToEmpty(result.refundInfo().refundMethod()),
                            nullToEmpty(result.refundInfo().refundStatus()),
                            formatInstant(result.refundInfo().refundedAt()),
                            nullToEmpty(result.refundInfo().pgRefundId()));
        }

        return new CancelDetailApiResponse(
                nullToEmpty(result.cancelId()),
                nullToEmpty(result.cancelNumber()),
                nullToEmpty(result.orderItemId()), // V4 간극
                result.cancelQty(),
                nullToEmpty(result.cancelType()),
                nullToEmpty(result.cancelStatus()),
                nullToEmpty(result.reasonType()),
                nullToEmpty(result.reasonDetail()),
                refundInfo,
                nullToEmpty(result.requestedBy()),
                nullToEmpty(result.processedBy()),
                formatInstant(result.requestedAt()),
                formatInstant(result.processedAt()),
                formatInstant(result.completedAt()),
                formatInstant(result.createdAt()),
                formatInstant(result.updatedAt()),
                toHistoryResponses(result.histories()));
    }

    private List<ClaimHistoryApiResponse> toHistoryResponses(List<ClaimHistoryResult> histories) {
        if (histories == null || histories.isEmpty()) {
            return List.of();
        }
        return histories.stream().map(this::toHistoryResponse).toList();
    }

    private ClaimHistoryApiResponse toHistoryResponse(ClaimHistoryResult history) {
        return new ClaimHistoryApiResponse(
                history.historyId(),
                history.type(),
                history.title(),
                history.message(),
                new ClaimHistoryApiResponse.ActorApiResponse(
                        history.actorType(), history.actorId(), history.actorName()),
                formatInstant(history.createdAt()));
    }

    public BatchResultApiResponse toBatchResultResponse(BatchProcessingResult<String> result) {
        List<BatchResultItemApiResponse> items =
                result.results().stream()
                        .map(
                                item ->
                                        new BatchResultItemApiResponse(
                                                item.id(),
                                                item.success(),
                                                item.errorCode(),
                                                item.errorMessage()))
                        .toList();
        return new BatchResultApiResponse(
                result.totalCount(), result.successCount(), result.failureCount(), items);
    }

    // ==================== 유틸 ====================

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private String formatInstant(Instant instant) {
        return instant != null ? KST_FORMATTER.format(instant) : null;
    }
}
