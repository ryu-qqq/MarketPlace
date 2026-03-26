package com.ryuqq.marketplace.adapter.in.rest.exchange.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ClaimOrderEnricher;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
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
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ApproveExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.CollectExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.CompleteExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ConvertToRefundBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.HoldExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.PrepareExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RejectExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeListResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Exchange API Mapper.
 *
 * <p>V4 간극 패턴:
 *
 * <ul>
 *   <li>orderId = 내부 orderItemId (프론트에겐 "주문 ID")
 *   <li>legacyOrderId 제외
 *   <li>Instant → ISO 8601 (+09:00) 문자열 변환
 *   <li>null 문자열 → ""
 *   <li>null 정수 → 0
 * </ul>
 */
@Component
public class ExchangeApiMapper {

    // ==================== Command 변환 ====================

    public RequestExchangeBatchCommand toRequestExchangeBatchCommand(
            RequestExchangeBatchApiRequest request, String requestedBy, long sellerId) {
        List<ExchangeRequestItem> items =
                request.items().stream().map(this::toExchangeRequestItem).toList();
        return new RequestExchangeBatchCommand(items, requestedBy, sellerId);
    }

    private ExchangeRequestItem toExchangeRequestItem(ExchangeRequestItemApiRequest item) {
        return new ExchangeRequestItem(
                item.orderId(), // V4 간극: orderId = 내부 orderItemId
                item.exchangeQty(),
                ExchangeReasonType.valueOf(item.reasonType()),
                item.reasonDetail(),
                item.originalProductId(),
                item.originalSkuCode(),
                item.targetProductGroupId(),
                item.targetProductId(),
                item.targetSkuCode(),
                item.targetQuantity());
    }

    public ApproveExchangeBatchCommand toApproveExchangeBatchCommand(
            ApproveExchangeBatchApiRequest request, String processedBy, Long sellerId) {
        return new ApproveExchangeBatchCommand(request.exchangeClaimIds(), processedBy, sellerId);
    }

    public CollectExchangeBatchCommand toCollectExchangeBatchCommand(
            CollectExchangeBatchApiRequest request, String processedBy, Long sellerId) {
        return new CollectExchangeBatchCommand(request.exchangeClaimIds(), processedBy, sellerId);
    }

    public PrepareExchangeBatchCommand toPrepareExchangeBatchCommand(
            PrepareExchangeBatchApiRequest request, String processedBy, Long sellerId) {
        return new PrepareExchangeBatchCommand(request.exchangeClaimIds(), processedBy, sellerId);
    }

    public RejectExchangeBatchCommand toRejectExchangeBatchCommand(
            RejectExchangeBatchApiRequest request, String processedBy, Long sellerId) {
        return new RejectExchangeBatchCommand(request.exchangeClaimIds(), processedBy, sellerId);
    }

    public ShipExchangeBatchCommand toShipCommand(
            ShipExchangeBatchApiRequest request, String processedBy, Long sellerId) {
        List<ShipExchangeBatchCommand.ShipItem> items =
                request.items().stream()
                        .map(
                                item ->
                                        new ShipExchangeBatchCommand.ShipItem(
                                                item.exchangeClaimId(),
                                                item.linkedOrderId(),
                                                item.deliveryCompany(),
                                                item.trackingNumber()))
                        .toList();
        return new ShipExchangeBatchCommand(items, processedBy, sellerId);
    }

    public CompleteExchangeBatchCommand toCompleteCommand(
            CompleteExchangeBatchApiRequest request, String processedBy, Long sellerId) {
        return new CompleteExchangeBatchCommand(request.exchangeClaimIds(), processedBy, sellerId);
    }

    public ConvertToRefundBatchCommand toConvertToRefundCommand(
            ConvertToRefundBatchApiRequest request, String processedBy, Long sellerId) {
        return new ConvertToRefundBatchCommand(request.exchangeClaimIds(), processedBy, sellerId);
    }

    public HoldExchangeBatchCommand toHoldCommand(
            HoldExchangeBatchApiRequest request, String processedBy, Long sellerId) {
        return new HoldExchangeBatchCommand(
                request.exchangeClaimIds(),
                request.isHold(),
                request.memo(),
                processedBy,
                sellerId);
    }

    // ==================== Query 변환 ====================

    public ExchangeSearchParams toSearchParams(ExchangeSearchApiRequest request) {
        return new ExchangeSearchParams(
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
            ExchangePageResult result, ClaimOrderEnricher enricher) {
        List<String> orderItemIds =
                result.exchanges().stream().map(ExchangeListResult::orderItemId).toList();
        ClaimOrderEnricher.OrderContext ctx = enricher.loadOrderContext(orderItemIds);

        List<ClaimListItemApiResponseV4> responses =
                result.exchanges().stream().map(r -> toListResponseV4(r, enricher, ctx)).toList();
        return PageApiResponse.of(
                responses,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    private ClaimListItemApiResponseV4 toListResponseV4(
            ExchangeListResult r,
            ClaimOrderEnricher enricher,
            ClaimOrderEnricher.OrderContext ctx) {
        String itemId = r.orderItemId();
        return new ClaimListItemApiResponseV4(
                enricher.toOrderProductV4(itemId, ctx),
                enricher.toClaimInfoV4(
                        r.exchangeClaimId(),
                        r.claimNumber(),
                        r.exchangeStatus(),
                        r.exchangeQty(),
                        r.reasonType(),
                        r.reasonDetail(),
                        null,
                        null,
                        "",
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

    public ExchangeSummaryApiResponse toSummaryResponse(ExchangeSummaryResult result) {
        return new ExchangeSummaryApiResponse(
                result.requested(),
                result.collecting(),
                result.collected(),
                result.preparing(),
                result.shipping(),
                result.completed(),
                result.rejected(),
                result.cancelled());
    }

    public PageApiResponse<ExchangeListApiResponse> toPageResponse(ExchangePageResult result) {
        List<ExchangeListApiResponse> responses =
                result.exchanges().stream().map(this::toListResponse).toList();
        return PageApiResponse.of(
                responses,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    private ExchangeListApiResponse toListResponse(ExchangeListResult result) {
        return new ExchangeListApiResponse(
                nullToEmpty(result.exchangeClaimId()),
                nullToEmpty(result.claimNumber()),
                nullToEmpty(result.orderItemId()), // V4 간극: orderId = orderItemId
                result.exchangeQty(),
                nullToEmpty(result.exchangeStatus()),
                nullToEmpty(result.reasonType()),
                nullToEmpty(result.reasonDetail()),
                nullToEmpty(result.targetSkuCode()),
                result.targetQuantity() != null ? result.targetQuantity() : 0,
                nullToEmpty(result.linkedOrderId()),
                nullToEmpty(result.requestedBy()),
                nullToEmpty(result.processedBy()),
                formatInstant(result.requestedAt()),
                formatInstant(result.processedAt()),
                formatInstant(result.completedAt()));
    }

    public ExchangeDetailApiResponse toDetailResponse(
            ExchangeDetailResult result,
            ClaimListItemApiResponseV4.OrderProductV4 orderProduct,
            ClaimListItemApiResponseV4.BuyerInfoV4 buyerInfo,
            ClaimListItemApiResponseV4.PaymentV4 payment,
            ClaimListItemApiResponseV4.ReceiverInfoV4 receiverInfo) {
        ExchangeDetailApiResponse.ExchangeOptionApiResponse exchangeOption = null;
        if (result.exchangeOption() != null) {
            exchangeOption =
                    new ExchangeDetailApiResponse.ExchangeOptionApiResponse(
                            result.exchangeOption().originalProductId(),
                            nullToEmpty(result.exchangeOption().originalSkuCode()),
                            result.exchangeOption().targetProductGroupId(),
                            result.exchangeOption().targetProductId(),
                            nullToEmpty(result.exchangeOption().targetSkuCode()),
                            result.exchangeOption().quantity());
        }

        ExchangeDetailApiResponse.AmountAdjustmentApiResponse amountAdjustment = null;
        if (result.amountAdjustment() != null) {
            amountAdjustment =
                    new ExchangeDetailApiResponse.AmountAdjustmentApiResponse(
                            result.amountAdjustment().originalPrice(),
                            result.amountAdjustment().targetPrice(),
                            result.amountAdjustment().priceDifference(),
                            result.amountAdjustment().additionalPaymentRequired(),
                            result.amountAdjustment().partialRefundRequired(),
                            result.amountAdjustment().collectShippingFee(),
                            result.amountAdjustment().reshipShippingFee(),
                            result.amountAdjustment().totalShippingFee(),
                            nullToEmpty(result.amountAdjustment().shippingFeePayer()));
        }

        ExchangeDetailApiResponse.CollectShipmentApiResponse collectShipment =
                new ExchangeDetailApiResponse.CollectShipmentApiResponse("", "", "");
        if (result.collectShipment() != null) {
            collectShipment =
                    new ExchangeDetailApiResponse.CollectShipmentApiResponse(
                            nullToEmpty(result.collectShipment().collectDeliveryCompany()),
                            nullToEmpty(result.collectShipment().collectTrackingNumber()),
                            nullToEmpty(result.collectShipment().collectStatus()));
        }

        ExchangeDetailApiResponse.ExchangeClaimInfoApiResponse claimInfo =
                new ExchangeDetailApiResponse.ExchangeClaimInfoApiResponse(
                        nullToEmpty(result.exchangeClaimId()),
                        nullToEmpty(result.claimNumber()),
                        result.sellerId(),
                        result.exchangeQty(),
                        nullToEmpty(result.exchangeStatus()),
                        nullToEmpty(result.reasonType()),
                        nullToEmpty(result.reasonDetail()),
                        exchangeOption,
                        amountAdjustment,
                        collectShipment,
                        nullToEmpty(result.linkedOrderId()),
                        formatInstant(result.requestedAt()),
                        formatInstant(result.completedAt()));

        List<ClaimListItemApiResponseV4.OrderProductV4> orderProducts =
                orderProduct != null ? List.of(orderProduct) : List.of();

        return new ExchangeDetailApiResponse(
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

    public AddClaimHistoryMemoCommand toAddMemoCommand(
            String exchangeClaimId,
            String orderItemId,
            AddClaimHistoryMemoApiRequest request,
            MarketAccessChecker.ActorInfo actor) {
        return new AddClaimHistoryMemoCommand(
                ClaimType.EXCHANGE,
                exchangeClaimId,
                orderItemId,
                request.message(),
                String.valueOf(actor.actorId()),
                actor.username());
    }

    // ==================== 유틸 ====================

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private String formatInstant(Instant instant) {
        return DateTimeFormatUtils.formatDisplay(instant);
    }
}
