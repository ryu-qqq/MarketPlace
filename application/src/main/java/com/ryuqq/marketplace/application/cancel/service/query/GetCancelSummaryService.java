package com.ryuqq.marketplace.application.cancel.service.query;

import com.ryuqq.marketplace.application.cancel.assembler.CancelAssembler;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelSummaryUseCase;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 취소 상태별 요약 조회 서비스. */
@Service
public class GetCancelSummaryService implements GetCancelSummaryUseCase {

    private final CancelReadManager cancelReadManager;
    private final CancelAssembler assembler;

    public GetCancelSummaryService(CancelReadManager cancelReadManager, CancelAssembler assembler) {
        this.cancelReadManager = cancelReadManager;
        this.assembler = assembler;
    }

    @Override
    public CancelSummaryResult execute() {
        Map<CancelStatus, Long> statusCounts = cancelReadManager.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
