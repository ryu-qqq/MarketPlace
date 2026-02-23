package com.ryuqq.marketplace.application.legacyproduct.service.query;

import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductIdResolver;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductIdResolver.ResolvedLegacyProductId;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 조회 서비스.
 *
 * <p>세토프 PK → 내부 ID 해석 후 기존 GetProductGroupUseCase에 위임합니다.
 */
@Service
public class LegacyProductQueryService implements LegacyProductQueryUseCase {

    private final LegacyProductIdResolver idResolver;
    private final GetProductGroupUseCase getProductGroupUseCase;

    public LegacyProductQueryService(
            LegacyProductIdResolver idResolver, GetProductGroupUseCase getProductGroupUseCase) {
        this.idResolver = idResolver;
        this.getProductGroupUseCase = getProductGroupUseCase;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductGroupDetailCompositeResult execute(long setofProductGroupId) {
        ResolvedLegacyProductId resolved = idResolver.resolve(setofProductGroupId);
        return getProductGroupUseCase.execute(resolved.internalProductGroupId());
    }
}
