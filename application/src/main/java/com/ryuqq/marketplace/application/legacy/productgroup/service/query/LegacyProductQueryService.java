package com.ryuqq.marketplace.application.legacy.productgroup.service.query;

import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.assembler.LegacyProductGroupFromMarketAssembler;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 조회 서비스.
 *
 * <p>레거시 PK를 market PK로 resolve 후, 표준 GetProductGroupUseCase로 market 스키마에서 조회합니다.
 */
@Service
public class LegacyProductQueryService implements LegacyProductQueryUseCase {

    private final LegacyProductIdResolveFactory resolveFactory;
    private final GetProductGroupUseCase getProductGroupUseCase;
    private final LegacyProductGroupFromMarketAssembler assembler;

    public LegacyProductQueryService(
            LegacyProductIdResolveFactory resolveFactory,
            GetProductGroupUseCase getProductGroupUseCase,
            LegacyProductGroupFromMarketAssembler assembler) {
        this.resolveFactory = resolveFactory;
        this.getProductGroupUseCase = getProductGroupUseCase;
        this.assembler = assembler;
    }

    @Override
    public LegacyProductGroupDetailResult execute(long productGroupId) {
        ResolvedLegacyProductIds resolved = resolveFactory.resolve(productGroupId);
        ProductGroupDetailCompositeResult composite =
                getProductGroupUseCase.execute(resolved.resolvedProductGroupId().value());
        return assembler.toDetailResult(composite, resolved);
    }
}
