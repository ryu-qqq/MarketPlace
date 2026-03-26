package com.ryuqq.marketplace.adapter.in.rest.refund.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
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
            String orderItemId,
            AddClaimHistoryMemoApiRequest request,
            MarketAccessChecker.ActorInfo actor) {
        return new AddClaimHistoryMemoCommand(
                ClaimType.REFUND,
                refundClaimId,
                orderItemId,
                request.message(),
                String.valueOf(actor.actorId()),
                actor.username());
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

    // ==================== V4 Response 변환 (프론트 중첩 구조) ====================

    public PageApiResponse<ClaimListItemApiResponseV4> toPageResponseV4(
            RefundPageResult result, ClaimOrderEnricher enricher) {
        List<String> orderItemIds =
                result.refunds().stream().map(RefundListResult::orderItemId).toList();
        ClaimOrderEnricher.OrderContext ctx = enricher.loadOrderContext(orderItemIds);

        List<ClaimListItemApiResponseV4> responses =
                result.refunds().stream().map(r -> toListResponseV4(r, enricher, ctx)).toList();
        return PageApiResponse.of(
                responses,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    private ClaimListItemApiResponseV4 toListResponseV4(
            RefundListResult r, ClaimOrderEnricher enricher, ClaimOrderEnricher.OrderContext ctx) {
        String itemId = r.orderItemId();
        return new ClaimListItemApiResponseV4(
                enricher.toOrderProductV4(itemId, ctx),
                enricher.toClaimInfoV4(
                        r.refundClaimId(),
                        r.claimNumber(),
                        r.refundStatus(),
                        r.refundQty(),
                        r.reasonType(),
                        r.reasonDetail(),
                        r.originalAmount(),
                        r.finalAmount(),
                        r.refundMethod(),
                        "",
                        false,
                        r.requestedAt(),
                        r.requestedAt(),
                        null,
                        null),
                enricher.toBuyerInfoV4(itemId, ctx),
                enricher.toPaymentV4(itemId, ctx),
                enricher.toReceiverInfoV4(itemId, ctx),
                enricher.toExternalOrderInfoV4(itemId, ctx));
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

    public RefundDetailApiResponse toDetailResponse(
            RefundDetailResult result,
            ClaimListItemApiResponseV4.OrderProductV4 orderProduct,
            ClaimListItemApiResponseV4.BuyerInfoV4 buyerInfo,
            ClaimListItemApiResponseV4.PaymentV4 payment,
            ClaimListItemApiResponseV4.ReceiverInfoV4 receiverInfo) {
        ClaimListItemApiResponseV4.RefundInfoV4 refundInfo =
                new ClaimListItemApiResponseV4.RefundInfoV4(0, 0, "", 0, "", "");
        if (result.refundInfo() != null) {
            refundInfo =
                    new ClaimListItemApiResponseV4.RefundInfoV4(
                            result.refundInfo().originalAmount(),
                            result.refundInfo().deductionAmount(),
                            nullToEmpty(result.refundInfo().deductionReason()),
                            result.refundInfo().finalAmount(),
                            nullToEmpty(result.refundInfo().refundMethod()),
                            formatInstant(result.refundInfo().refundedAt()));
        }

        RefundDetailApiResponse.HoldInfoApiResponse holdInfo =
                new RefundDetailApiResponse.HoldInfoApiResponse("", "");
        if (result.holdInfo() != null) {
            holdInfo =
                    new RefundDetailApiResponse.HoldInfoApiResponse(
                            nullToEmpty(result.holdInfo().holdReason()),
                            formatInstant(result.holdInfo().holdAt()));
        }

        RefundDetailApiResponse.CollectShipmentApiResponse collectShipment =
                new RefundDetailApiResponse.CollectShipmentApiResponse("", "", "");
        if (result.collectShipment() != null) {
            collectShipment =
                    new RefundDetailApiResponse.CollectShipmentApiResponse(
                            nullToEmpty(result.collectShipment().collectDeliveryCompany()),
                            nullToEmpty(result.collectShipment().collectTrackingNumber()),
                            nullToEmpty(result.collectShipment().collectStatus()));
        }

        RefundDetailApiResponse.RefundClaimInfoApiResponse claimInfo =
                new RefundDetailApiResponse.RefundClaimInfoApiResponse(
                        nullToEmpty(result.refundClaimId()),
                        nullToEmpty(result.claimNumber()),
                        result.refundQty(),
                        nullToEmpty(result.refundStatus()),
                        nullToEmpty(result.reasonType()),
                        nullToEmpty(result.reasonDetail()),
                        refundInfo,
                        holdInfo,
                        collectShipment,
                        formatInstant(result.requestedAt()),
                        formatInstant(result.completedAt()));

        List<ClaimListItemApiResponseV4.OrderProductV4> orderProducts =
                orderProduct != null ? List.of(orderProduct) : List.of();

        return new RefundDetailApiResponse(
                nullToEmpty(result.orderItemId()), // V4 간극
                orderProducts,
                claimInfo,
                buyerInfo,
                payment,
                receiverInfo,
                nullToEmpty(result.requestedBy()),
                nullToEmpty(result.processedBy()),
                formatInstant(result.processedAt()),
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
        return DateTimeFormatUtils.formatDisplay(instant);
    }
}
