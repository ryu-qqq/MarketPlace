package com.ryuqq.marketplace.application.exchange.service.query;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
import com.ryuqq.marketplace.application.exchange.assembler.ExchangeAssembler;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeDetailUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import java.util.List;
import org.springframework.stereotype.Service;

/** 교환 상세 조회 서비스. */
@Service
public class GetExchangeDetailService implements GetExchangeDetailUseCase {

    private final ExchangeReadManager exchangeReadManager;
    private final ExchangeAssembler assembler;
    private final ClaimHistoryReadManager historyReadManager;

    public GetExchangeDetailService(
            ExchangeReadManager exchangeReadManager,
            ExchangeAssembler assembler,
            ClaimHistoryReadManager historyReadManager) {
        this.exchangeReadManager = exchangeReadManager;
        this.assembler = assembler;
        this.historyReadManager = historyReadManager;
    }

    @Override
    public ExchangeDetailResult execute(String exchangeClaimId) {
        ExchangeClaim claim =
                exchangeReadManager.getById(ExchangeClaimId.of(exchangeClaimId));
        List<ClaimHistory> histories =
                historyReadManager.findByClaimId(ClaimType.EXCHANGE, exchangeClaimId);
        return assembler.toDetailResult(claim, histories);
    }
}
