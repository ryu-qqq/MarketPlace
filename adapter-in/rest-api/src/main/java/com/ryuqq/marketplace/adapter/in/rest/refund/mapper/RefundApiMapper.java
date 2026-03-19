package com.ryuqq.marketplace.adapter.in.rest.refund.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.ApproveRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.HoldRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RefundSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RejectRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RequestRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RequestRefundBatchApiRequest.RefundRequestItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.ApproveRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.HoldRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RejectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.application.refund.dto.query.RefundSearchParams;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Refund API Mapper.
 *
 * <p>V4 간극 패턴:
 *
 * <ul>
 *   <li>orderId = 내부 orderItemId (프론트에겐 "주문 ID")
 *   <li>legacyOrderId 제외
 *   <li>Instant → ISO 8601 (+09:00) 문자열 변환
 *   <li>null 문자열 → ""
 *   <li>null 금액 → 0
 * </ul>
 */
@Component
public class RefundApiMapper {

    private static final DateTimeFormatter KST_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                    .withZone(ZoneId.of("Asia/Seoul"));

    // ==================== Command 변환 ====================

    public RequestRefundBatchCommand toRequestRefundBatchCommand(
            RequestRefundBatchApiRequest request, String requestedBy, long sellerId) {
        List<RefundRequestItem> items =
                request.items().stream().map(this::toRefundRequestItem).toList();
        return new RequestRefundBatchCommand(items, requestedBy, sellerId);
    }

    private RefundRequestItem toRefundRequestItem(RefundRequestItemApiRequest item) {
        return new RefundRequestItem(
                item.orderId(), // V4 간극: orderId = 내부 orderItemId
                item.refundQty(),
                RefundReasonType.valueOf(item.reasonType()),
                item.reasonDetail());
    }

    public ApproveRefundBatchCommand toApproveRefundBatchCommand(
            ApproveRefundBatchApiRequest request, String processedBy, Long sellerId) {
        return new ApproveRefundBatchCommand(request.refundClaimIds(), processedBy, sellerId);
    }

    public RejectRefundBatchCommand toRejectRefundBatchCommand(
            RejectRefundBatchApiRequest request, String processedBy, Long sellerId) {
        return new RejectRefundBatchCommand(request.refundClaimIds(), processedBy, sellerId);
    }

    public HoldRefundBatchCommand toHoldCommand(
            HoldRefundBatchApiRequest request, String processedBy, Long sellerId) {
        return new HoldRefundBatchCommand(
                request.refundClaimIds(), request.isHold(), request.memo(), processedBy, sellerId);
    }

    public AddClaimHistoryMemoCommand toAddMemoCommand(
            String refundClaimId,
            AddClaimHistoryMemoApiRequest request,
            long sellerId,
            String actorName) {
        return new AddClaimHistoryMemoCommand(
                ClaimType.REFUND,
                refundClaimId,
                request.message(),
                String.valueOf(sellerId),
                actorName);
    }

    // ==================== Query 변환 ====================

    public RefundSearchParams toSearchParams(RefundSearchApiRequest request) {
        return new RefundSearchParams(
                request.statuses(),
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

    // ==================== Response 변환 ====================

    public RefundSummaryApiResponse toSummaryResponse(RefundSummaryResult result) {
        return new RefundSummaryApiResponse(
                result.requested(),
                result.collecting(),
                result.collected(),
                result.completed(),
                result.rejected(),
                result.cancelled());
    }

    public PageApiResponse<RefundListApiResponse> toPageResponse(RefundPageResult result) {
        List<RefundListApiResponse> responses =
                result.refunds().stream().map(this::toListResponse).toList();
        return PageApiResponse.of(
                responses,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    public RefundListApiResponse toListResponse(RefundListResult result) {
        return new RefundListApiResponse(
                nullToEmpty(result.refundClaimId()),
                nullToEmpty(result.claimNumber()),
                nullToEmpty(result.orderItemId()), // V4 간극: orderId = orderItemId
                result.refundQty(),
                nullToEmpty(result.refundStatus()),
                nullToEmpty(result.reasonType()),
                nullToEmpty(result.reasonDetail()),
                result.originalAmount() != null ? result.originalAmount() : 0,
                result.finalAmount() != null ? result.finalAmount() : 0,
                nullToEmpty(result.refundMethod()),
                nullToEmpty(result.requestedBy()),
                nullToEmpty(result.processedBy()),
                formatInstant(result.requestedAt()),
                formatInstant(result.processedAt()),
                formatInstant(result.completedAt()));
    }

    public RefundDetailApiResponse toDetailResponse(RefundDetailResult result) {
        RefundDetailApiResponse.RefundInfoApiResponse refundInfo = null;
        if (result.refundInfo() != null) {
            refundInfo =
                    new RefundDetailApiResponse.RefundInfoApiResponse(
                            result.refundInfo().originalAmount(),
                            result.refundInfo().finalAmount(),
                            result.refundInfo().deductionAmount(),
                            nullToEmpty(result.refundInfo().deductionReason()),
                            nullToEmpty(result.refundInfo().refundMethod()),
                            formatInstant(result.refundInfo().refundedAt()));
        }

        RefundDetailApiResponse.HoldInfoApiResponse holdInfo = null;
        if (result.holdInfo() != null) {
            holdInfo =
                    new RefundDetailApiResponse.HoldInfoApiResponse(
                            nullToEmpty(result.holdInfo().holdReason()),
                            formatInstant(result.holdInfo().holdAt()));
        }

        return new RefundDetailApiResponse(
                nullToEmpty(result.refundClaimId()),
                nullToEmpty(result.claimNumber()),
                nullToEmpty(result.orderItemId()), // V4 간극
                result.refundQty(),
                nullToEmpty(result.refundStatus()),
                nullToEmpty(result.reasonType()),
                nullToEmpty(result.reasonDetail()),
                refundInfo,
                holdInfo,
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
