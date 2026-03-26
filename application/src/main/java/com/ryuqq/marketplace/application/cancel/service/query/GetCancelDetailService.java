package com.ryuqq.marketplace.application.cancel.service.query;

import com.ryuqq.marketplace.application.cancel.assembler.CancelAssembler;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelDetailUseCase;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.util.List;
import org.springframework.stereotype.Service;

/** 취소 상세 조회 서비스. */
@Service
public class GetCancelDetailService implements GetCancelDetailUseCase {

    private final CancelReadManager cancelReadManager;
    private final CancelAssembler assembler;
    private final ClaimHistoryReadManager historyReadManager;

    public GetCancelDetailService(
            CancelReadManager cancelReadManager,
            CancelAssembler assembler,
            ClaimHistoryReadManager historyReadManager) {
        this.cancelReadManager = cancelReadManager;
        this.assembler = assembler;
        this.historyReadManager = historyReadManager;
    }

    @Override
    public CancelDetailResult execute(String cancelId) {
        Cancel cancel = cancelReadManager.getById(CancelId.of(cancelId));
        List<ClaimHistory> histories = historyReadManager.findByClaimId(ClaimType.CANCEL, cancelId);
        return assembler.toDetailResult(cancel, histories);
    }
}
