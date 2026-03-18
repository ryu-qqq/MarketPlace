package com.ryuqq.marketplace.application.cancel.service.query;

import com.ryuqq.marketplace.application.cancel.assembler.CancelAssembler;
import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.factory.CancelQueryFactory;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelListUseCase;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 취소 목록 조회 서비스. */
@Service
public class GetCancelListService implements GetCancelListUseCase {

    private final CancelReadManager cancelReadManager;
    private final CancelQueryFactory queryFactory;
    private final CancelAssembler assembler;

    public GetCancelListService(
            CancelReadManager cancelReadManager,
            CancelQueryFactory queryFactory,
            CancelAssembler assembler) {
        this.cancelReadManager = cancelReadManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CancelPageResult execute(CancelSearchParams params) {
        CancelSearchCriteria criteria = queryFactory.createCriteria(params);
        List<Cancel> cancels = cancelReadManager.findByCriteria(criteria);
        long totalCount = cancelReadManager.countByCriteria(criteria);

        List<CancelListResult> results = cancels.stream().map(assembler::toListResult).toList();
        return assembler.toPageResult(results, params.page(), params.size(), totalCount);
    }
}
