package com.ryuqq.marketplace.application.cancel.assembler;

import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.vo.CancelRefundInfo;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Cancel 도메인 → Response DTO 변환. */
@Component
public class CancelAssembler {

    private final ClaimHistoryAssembler historyAssembler;

    public CancelAssembler(ClaimHistoryAssembler historyAssembler) {
        this.historyAssembler = historyAssembler;
    }

    public CancelListResult toListResult(Cancel cancel) {
        CancelRefundInfo refund = cancel.refundInfo();
        return new CancelListResult(
                cancel.idValue(),
                cancel.cancelNumberValue(),
                cancel.orderItemIdValue(),
                cancel.cancelQty(),
                cancel.type().name(),
                cancel.status().name(),
                cancel.reason().reasonType().name(),
                cancel.reason().reasonDetail(),
                refund != null ? refund.refundAmount().value() : null,
                refund != null ? refund.refundMethod() : null,
                cancel.requestedBy(),
                cancel.processedBy(),
                cancel.requestedAt(),
                cancel.processedAt(),
                cancel.completedAt());
    }

    public CancelDetailResult toDetailResult(Cancel cancel, List<ClaimHistory> histories) {
        CancelRefundInfo refund = cancel.refundInfo();
        CancelDetailResult.RefundInfo refundInfo = null;
        if (refund != null) {
            refundInfo =
                    new CancelDetailResult.RefundInfo(
                            refund.refundAmount().value(),
                            refund.refundMethod(),
                            refund.refundStatus(),
                            refund.refundedAt(),
                            refund.pgRefundId());
        }

        return new CancelDetailResult(
                cancel.idValue(),
                cancel.cancelNumberValue(),
                cancel.orderItemIdValue(),
                cancel.cancelQty(),
                cancel.type().name(),
                cancel.status().name(),
                cancel.reason().reasonType().name(),
                cancel.reason().reasonDetail(),
                refundInfo,
                cancel.requestedBy(),
                cancel.processedBy(),
                cancel.requestedAt(),
                cancel.processedAt(),
                cancel.completedAt(),
                cancel.createdAt(),
                cancel.updatedAt(),
                historyAssembler.toResults(histories));
    }

    public CancelPageResult toPageResult(
            List<CancelListResult> results, int page, int size, long totalCount) {
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new CancelPageResult(results, pageMeta);
    }

    public CancelSummaryResult toSummaryResult(Map<CancelStatus, Long> statusCounts) {
        return new CancelSummaryResult(
                statusCounts.getOrDefault(CancelStatus.REQUESTED, 0L),
                statusCounts.getOrDefault(CancelStatus.APPROVED, 0L),
                statusCounts.getOrDefault(CancelStatus.REJECTED, 0L),
                statusCounts.getOrDefault(CancelStatus.COMPLETED, 0L),
                statusCounts.getOrDefault(CancelStatus.CANCELLED, 0L));
    }
}
