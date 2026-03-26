package com.ryuqq.marketplace.application.refund.assembler;

import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult.CollectShipmentResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.vo.HoldInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** RefundClaim 도메인 -> Response DTO 변환. */
@Component
public class RefundAssembler {

    private final ClaimHistoryAssembler historyAssembler;

    public RefundAssembler(ClaimHistoryAssembler historyAssembler) {
        this.historyAssembler = historyAssembler;
    }

    public RefundListResult toListResult(RefundClaim claim) {
        RefundInfo refund = claim.refundInfo();
        return new RefundListResult(
                claim.idValue(),
                claim.claimNumberValue(),
                claim.orderItemIdValue(),
                claim.refundQty(),
                claim.status().name(),
                claim.reason().reasonType().name(),
                claim.reason().reasonDetail(),
                refund != null ? refund.originalAmount().value() : null,
                refund != null ? refund.finalAmount().value() : null,
                refund != null ? refund.refundMethod() : null,
                claim.requestedBy(),
                claim.processedBy(),
                claim.requestedAt(),
                claim.processedAt(),
                claim.completedAt());
    }

    public RefundDetailResult toDetailResult(RefundClaim claim, List<ClaimHistory> histories) {
        RefundInfo refund = claim.refundInfo();
        RefundDetailResult.RefundInfoResult refundInfoResult = null;
        if (refund != null) {
            refundInfoResult =
                    new RefundDetailResult.RefundInfoResult(
                            refund.originalAmount().value(),
                            refund.finalAmount().value(),
                            refund.deductionAmount().value(),
                            refund.deductionReason(),
                            refund.refundMethod(),
                            refund.refundedAt());
        }

        HoldInfo hold = claim.holdInfo();
        RefundDetailResult.HoldInfoResult holdInfoResult = null;
        if (hold != null) {
            holdInfoResult =
                    new RefundDetailResult.HoldInfoResult(hold.holdReason(), hold.holdAt());
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

        return new RefundDetailResult(
                claim.idValue(),
                claim.claimNumberValue(),
                claim.orderItemIdValue(),
                claim.refundQty(),
                claim.status().name(),
                claim.reason().reasonType().name(),
                claim.reason().reasonDetail(),
                refundInfoResult,
                holdInfoResult,
                collectShipmentResult,
                claim.requestedBy(),
                claim.processedBy(),
                claim.requestedAt(),
                claim.processedAt(),
                claim.completedAt(),
                claim.createdAt(),
                claim.updatedAt(),
                historyAssembler.toResults(histories));
    }

    public RefundPageResult toPageResult(
            List<RefundListResult> results, int page, int size, long totalCount) {
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new RefundPageResult(results, pageMeta);
    }

    public RefundSummaryResult toSummaryResult(Map<RefundStatus, Long> statusCounts) {
        return new RefundSummaryResult(
                statusCounts.getOrDefault(RefundStatus.REQUESTED, 0L),
                statusCounts.getOrDefault(RefundStatus.COLLECTING, 0L),
                statusCounts.getOrDefault(RefundStatus.COLLECTED, 0L),
                statusCounts.getOrDefault(RefundStatus.COMPLETED, 0L),
                statusCounts.getOrDefault(RefundStatus.REJECTED, 0L),
                statusCounts.getOrDefault(RefundStatus.CANCELLED, 0L));
    }
}
