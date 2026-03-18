package com.ryuqq.marketplace.application.refund.service.query;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
import com.ryuqq.marketplace.application.refund.assembler.RefundAssembler;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundDetailUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import java.util.List;
import org.springframework.stereotype.Service;

/** 환불 상세 조회 서비스. */
@Service
public class GetRefundDetailService implements GetRefundDetailUseCase {

    private final RefundReadManager refundReadManager;
    private final RefundAssembler assembler;
    private final ClaimHistoryReadManager historyReadManager;

    public GetRefundDetailService(
            RefundReadManager refundReadManager,
            RefundAssembler assembler,
            ClaimHistoryReadManager historyReadManager) {
        this.refundReadManager = refundReadManager;
        this.assembler = assembler;
        this.historyReadManager = historyReadManager;
    }

    @Override
    public RefundDetailResult execute(String refundClaimId) {
        RefundClaim claim = refundReadManager.getById(RefundClaimId.of(refundClaimId));
        List<ClaimHistory> histories =
                historyReadManager.findByClaimId(ClaimType.REFUND, refundClaimId);
        return assembler.toDetailResult(claim, histories);
    }
}
