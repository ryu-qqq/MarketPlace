package com.ryuqq.marketplace.application.cancel.service.query;

import com.ryuqq.marketplace.application.cancel.assembler.CancelAssembler;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelSummaryUseCase;
import com.ryuqq.marketplace.application.cancel.port.out.query.CancelQueryPort;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 취소 상태별 요약 조회 서비스. */
@Service
public class GetCancelSummaryService implements GetCancelSummaryUseCase {

    private final CancelQueryPort cancelQueryPort;
    private final CancelAssembler assembler;

    public GetCancelSummaryService(CancelQueryPort cancelQueryPort, CancelAssembler assembler) {
        this.cancelQueryPort = cancelQueryPort;
        this.assembler = assembler;
    }

    @Override
    public CancelSummaryResult execute() {
        Map<CancelStatus, Long> statusCounts = cancelQueryPort.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
