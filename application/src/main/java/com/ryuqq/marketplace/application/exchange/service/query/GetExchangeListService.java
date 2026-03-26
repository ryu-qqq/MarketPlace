package com.ryuqq.marketplace.application.exchange.service.query;

import com.ryuqq.marketplace.application.exchange.assembler.ExchangeAssembler;
import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeListResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeQueryFactory;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.exchange.port.in.query.GetExchangeListUseCase;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 교환 목록 조회 서비스. */
@Service
public class GetExchangeListService implements GetExchangeListUseCase {

    private final ExchangeReadManager exchangeReadManager;
    private final ExchangeQueryFactory queryFactory;
    private final ExchangeAssembler assembler;

    public GetExchangeListService(
            ExchangeReadManager exchangeReadManager,
            ExchangeQueryFactory queryFactory,
            ExchangeAssembler assembler) {
        this.exchangeReadManager = exchangeReadManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public ExchangePageResult execute(ExchangeSearchParams params) {
        ExchangeSearchCriteria criteria = queryFactory.createCriteria(params);
        List<ExchangeClaim> claims = exchangeReadManager.findByCriteria(criteria);
        long totalCount = exchangeReadManager.countByCriteria(criteria);

        List<ExchangeListResult> results = claims.stream().map(assembler::toListResult).toList();
        return assembler.toPageResult(results, params.page(), params.size(), totalCount);
    }
}
