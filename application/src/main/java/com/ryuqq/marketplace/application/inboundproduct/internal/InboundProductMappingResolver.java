package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingReadManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingReadManager;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 외부 브랜드/카테고리 코드를 내부 ID로 매핑 해석. */
@Component
public class InboundProductMappingResolver {

    private static final Logger log = LoggerFactory.getLogger(InboundProductMappingResolver.class);

    private final InboundBrandMappingReadManager brandMappingReadManager;
    private final InboundCategoryMappingReadManager categoryMappingReadManager;

    public InboundProductMappingResolver(
            InboundBrandMappingReadManager brandMappingReadManager,
            InboundCategoryMappingReadManager categoryMappingReadManager) {
        this.brandMappingReadManager = brandMappingReadManager;
        this.categoryMappingReadManager = categoryMappingReadManager;
    }

    public Optional<Long> resolveInternalBrandId(long inboundSourceId, String externalBrandCode) {
        return brandMappingReadManager
                .findOptionalBySourceIdAndCode(inboundSourceId, externalBrandCode)
                .filter(InboundBrandMapping::isActive)
                .map(InboundBrandMapping::internalBrandId);
    }

    public Optional<Long> resolveInternalCategoryId(
            long inboundSourceId, String externalCategoryCode) {
        return categoryMappingReadManager
                .findOptionalBySourceIdAndCode(inboundSourceId, externalCategoryCode)
                .filter(InboundCategoryMapping::isActive)
                .map(InboundCategoryMapping::internalCategoryId);
    }

    public InboundProductMappingResult resolveMapping(
            long inboundSourceId, String brandCode, String categoryCode) {
        Long brandId = resolveInternalBrandId(inboundSourceId, brandCode).orElse(null);
        Long categoryId = resolveInternalCategoryId(inboundSourceId, categoryCode).orElse(null);
        return InboundProductMappingResult.of(brandId, categoryId);
    }

    public InboundProductMappingResult resolveMapping(InboundProduct product) {
        return resolveMapping(
                product.inboundSourceId(),
                product.externalBrandCode(),
                product.externalCategoryCode());
    }

    public InboundProductMappingResult resolveMappingAndApply(InboundProduct product, Instant now) {
        InboundProductMappingResult mapping = resolveMapping(product);

        if (mapping.isFullyMapped()) {
            product.applyMapping(mapping.internalBrandId(), mapping.internalCategoryId(), now);
        } else {
            product.markMappingFailed(now);
            log.warn(
                    "인바운드 상품 매핑 실패: inboundSourceId={}, code={}, brandId={}, categoryId={}",
                    product.inboundSourceId(),
                    product.externalProductCodeValue(),
                    mapping.internalBrandId(),
                    mapping.internalCategoryId());
        }
        return mapping;
    }
}
