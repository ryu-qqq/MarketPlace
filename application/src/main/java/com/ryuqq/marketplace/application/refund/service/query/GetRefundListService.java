package com.ryuqq.marketplace.application.refund.service.query;

import com.ryuqq.marketplace.application.refund.assembler.RefundAssembler;
import com.ryuqq.marketplace.application.refund.dto.query.RefundSearchParams;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.factory.RefundQueryFactory;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.application.refund.port.in.query.GetRefundListUseCase;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 환불 목록 조회 서비스. */
@Service
public class GetRefundListService implements GetRefundListUseCase {

    private final RefundReadManager refundReadManager;
    private final RefundQueryFactory queryFactory;
    private final RefundAssembler assembler;

    public GetRefundListService(
            RefundReadManager refundReadManager,
            RefundQueryFactory queryFactory,
            RefundAssembler assembler) {
        this.refundReadManager = refundReadManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public RefundPageResult execute(RefundSearchParams params) {
        RefundSearchCriteria criteria = queryFactory.createCriteria(params);
        List<RefundClaim> claims = refundReadManager.findByCriteria(criteria);
        long totalCount = refundReadManager.countByCriteria(criteria);

        List<RefundListResult> results = claims.stream().map(assembler::toListResult).toList();
        return assembler.toPageResult(results, params.page(), params.size(), totalCount);
    }
}
