package com.ryuqq.marketplace.application.exchange.service.query;

import com.ryuqq.marketplace.application.exchange.assembler.ExchangeAssembler;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeSummaryUseCase;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 교환 상태별 요약 조회 서비스. */
@Service
public class GetExchangeSummaryService implements GetExchangeSummaryUseCase {

    private final ExchangeReadManager exchangeReadManager;
    private final ExchangeAssembler assembler;

    public GetExchangeSummaryService(
            ExchangeReadManager exchangeReadManager, ExchangeAssembler assembler) {
        this.exchangeReadManager = exchangeReadManager;
        this.assembler = assembler;
    }

    @Override
    public ExchangeSummaryResult execute() {
        Map<ExchangeStatus, Long> statusCounts = exchangeReadManager.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
