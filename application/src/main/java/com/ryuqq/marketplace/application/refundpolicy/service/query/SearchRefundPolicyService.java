package com.ryuqq.marketplace.application.refundpolicy.service.query;

import com.ryuqq.marketplace.application.refundpolicy.assembler.RefundPolicyAssembler;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyQueryFactory;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.refundpolicy.port.in.query.SearchRefundPolicyUseCase;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 환불 정책 검색 Service.
 *
 * <p>QueryFactory를 통해 Params → Criteria 변환
 *
 * <p>Assembler를 통해 RefundPolicyPageResult 생성
 */
@Service
public class SearchRefundPolicyService implements SearchRefundPolicyUseCase {

    private final RefundPolicyReadManager readManager;
    private final RefundPolicyQueryFactory queryFactory;
    private final RefundPolicyAssembler assembler;

    public SearchRefundPolicyService(
            RefundPolicyReadManager readManager,
            RefundPolicyQueryFactory queryFactory,
            RefundPolicyAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public RefundPolicyPageResult execute(RefundPolicySearchParams params) {
        RefundPolicySearchCriteria criteria = queryFactory.createCriteria(params);

        List<RefundPolicy> domains = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(domains, params.page(), params.size(), totalElements);
    }
}
