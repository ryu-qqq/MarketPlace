package com.ryuqq.marketplace.application.exchange.assembler;

import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult.AmountAdjustmentResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult.CollectShipmentResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult.ExchangeOptionResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeListResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.vo.AmountAdjustment;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeOption;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** ExchangeClaim 도메인 -> Response DTO 변환. */
@Component
public class ExchangeAssembler {

    private final ClaimHistoryAssembler historyAssembler;

    public ExchangeAssembler(ClaimHistoryAssembler historyAssembler) {
        this.historyAssembler = historyAssembler;
    }

    public ExchangeListResult toListResult(ExchangeClaim claim) {
        ExchangeOption option = claim.exchangeOption();
        return new ExchangeListResult(
                claim.idValue(),
                claim.claimNumberValue(),
                claim.orderItemIdValue(),
                claim.exchangeQty(),
                claim.status().name(),
                claim.reason().reasonType().name(),
                claim.reason().reasonDetail(),
                option != null ? option.originalSkuCode() : null,
                option != null ? option.targetSkuCode() : null,
                option != null ? option.quantity() : null,
                claim.linkedOrderId(),
                claim.requestedBy(),
                claim.processedBy(),
                claim.requestedAt(),
                claim.processedAt(),
                claim.completedAt());
    }

    public ExchangeDetailResult toDetailResult(ExchangeClaim claim, List<ClaimHistory> histories) {
        ExchangeOption option = claim.exchangeOption();
        ExchangeOptionResult exchangeOptionResult = null;
        if (option != null) {
            exchangeOptionResult =
                    new ExchangeOptionResult(
                            option.originalProductId(),
                            option.originalSkuCode(),
                            option.targetProductGroupId(),
                            option.targetProductId(),
                            option.targetSkuCode(),
                            option.quantity());
        }

        AmountAdjustment adjustment = claim.amountAdjustment();
        AmountAdjustmentResult amountAdjustmentResult = null;
        if (adjustment != null) {
            amountAdjustmentResult =
                    new AmountAdjustmentResult(
                            adjustment.originalPrice().value(),
                            adjustment.targetPrice().value(),
                            adjustment.priceDifference().value(),
                            adjustment.additionalPaymentRequired(),
                            adjustment.partialRefundRequired(),
                            adjustment.collectShippingFee().value(),
                            adjustment.reshipShippingFee().value(),
                            adjustment.totalShippingFee().value(),
                            adjustment.shippingFeePayer().name());
        }

        ClaimShipment collectShipment = claim.collectShipment();
        CollectShipmentResult collectShipmentResult = null;
        if (collectShipment != null) {
            collectShipmentResult =
                    new CollectShipmentResult(
                            collectShipment.method() != null
                                    ? collectShipment.method().courierName()
                                    : null,
                            collectShipment.trackingNumber(),
                            collectShipment.status().name());
        }

        return new ExchangeDetailResult(
                claim.idValue(),
                claim.claimNumberValue(),
                claim.orderItemIdValue(),
                claim.sellerId(),
                claim.exchangeQty(),
                claim.status().name(),
                claim.reason().reasonType().name(),
                claim.reason().reasonDetail(),
                exchangeOptionResult,
                amountAdjustmentResult,
                collectShipmentResult,
                claim.linkedOrderId(),
                claim.requestedBy(),
                claim.processedBy(),
                claim.requestedAt(),
                claim.processedAt(),
                claim.completedAt(),
                claim.createdAt(),
                claim.updatedAt(),
                historyAssembler.toResults(histories));
    }

    public ExchangePageResult toPageResult(
            List<ExchangeListResult> results, int page, int size, long totalCount) {
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new ExchangePageResult(results, pageMeta);
    }

    public ExchangeSummaryResult toSummaryResult(Map<ExchangeStatus, Long> statusCounts) {
        return new ExchangeSummaryResult(
                statusCounts.getOrDefault(ExchangeStatus.REQUESTED, 0L),
                statusCounts.getOrDefault(ExchangeStatus.COLLECTING, 0L),
                statusCounts.getOrDefault(ExchangeStatus.COLLECTED, 0L),
                statusCounts.getOrDefault(ExchangeStatus.PREPARING, 0L),
                statusCounts.getOrDefault(ExchangeStatus.SHIPPING, 0L),
                statusCounts.getOrDefault(ExchangeStatus.COMPLETED, 0L),
                statusCounts.getOrDefault(ExchangeStatus.REJECTED, 0L),
                statusCounts.getOrDefault(ExchangeStatus.CANCELLED, 0L));
    }
}
