package com.ryuqq.marketplace.application.refund.service.query;

import com.ryuqq.marketplace.application.refund.assembler.RefundAssembler;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundSummaryUseCase;
import com.ryuqq.marketplace.application.refund.port.out.query.RefundQueryPort;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 환불 상태별 요약 조회 서비스. */
@Service
public class GetRefundSummaryService implements GetRefundSummaryUseCase {

    private final RefundQueryPort refundQueryPort;
    private final RefundAssembler assembler;

    public GetRefundSummaryService(RefundQueryPort refundQueryPort, RefundAssembler assembler) {
        this.refundQueryPort = refundQueryPort;
        this.assembler = assembler;
    }

    @Override
    public RefundSummaryResult execute() {
        Map<RefundStatus, Long> statusCounts = refundQueryPort.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
