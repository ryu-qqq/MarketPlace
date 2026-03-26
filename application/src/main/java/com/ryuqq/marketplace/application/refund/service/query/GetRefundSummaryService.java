package com.ryuqq.marketplace.application.refund.service.query;

import com.ryuqq.marketplace.application.refund.assembler.RefundAssembler;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundSummaryUseCase;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 환불 상태별 요약 조회 서비스. */
@Service
public class GetRefundSummaryService implements GetRefundSummaryUseCase {

    private final RefundReadManager refundReadManager;
    private final RefundAssembler assembler;

    public GetRefundSummaryService(RefundReadManager refundReadManager, RefundAssembler assembler) {
        this.refundReadManager = refundReadManager;
        this.assembler = assembler;
    }

    @Override
    public RefundSummaryResult execute() {
        Map<RefundStatus, Long> statusCounts = refundReadManager.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
