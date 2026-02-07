package com.ryuqq.marketplace.application.shippingpolicy.service.query;

import com.ryuqq.marketplace.application.shippingpolicy.assembler.ShippingPolicyAssembler;
import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyQueryFactory;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.query.SearchShippingPolicyUseCase;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 배송 정책 검색 Service.
 *
 * <p>QueryFactory를 통해 Params → Criteria 변환
 *
 * <p>Assembler를 통해 ShippingPolicyPageResult 생성
 */
@Service
public class SearchShippingPolicyService implements SearchShippingPolicyUseCase {

    private final ShippingPolicyReadManager readManager;
    private final ShippingPolicyQueryFactory queryFactory;
    private final ShippingPolicyAssembler assembler;

    public SearchShippingPolicyService(
            ShippingPolicyReadManager readManager,
            ShippingPolicyQueryFactory queryFactory,
            ShippingPolicyAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public ShippingPolicyPageResult execute(ShippingPolicySearchParams params) {
        ShippingPolicySearchCriteria criteria = queryFactory.createCriteria(params);

        List<ShippingPolicy> domains = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(domains, params.page(), params.size(), totalElements);
    }
}
