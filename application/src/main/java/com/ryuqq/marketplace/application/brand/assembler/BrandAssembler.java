package com.ryuqq.marketplace.application.brand.assembler;

import com.ryuqq.marketplace.application.brand.dto.response.BrandAliasResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandDetailResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResponse;
import com.ryuqq.marketplace.application.brand.dto.response.BrandSimpleResponse;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.BrandAlias;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Brand Assembler
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지</li>
 *   <li>Law of Demeter 준수 - Domain 객체 메서드 직접 호출</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Component
public class BrandAssembler {
    
    public BrandResponse toResponse(Brand brand) {
        return new BrandResponse(
            brand.id() != null ? brand.id().value() : null,
            brand.code().value(),
            brand.canonicalName().value(),
            brand.nameKo(),
            brand.nameEn(),
            brand.shortName(),
            brand.country() != null ? brand.country().code() : null,
            brand.department() != null ? brand.department().name() : null,
            brand.isLuxury(),
            brand.status().name(),
            brand.logoUrl()
        );
    }
    
    public BrandDetailResponse toDetailResponse(Brand brand) {
        List<BrandAliasResponse> aliasResponses = brand.aliases().stream()
            .map(this::toAliasResponse)
            .toList();
        
        return new BrandDetailResponse(
            brand.id() != null ? brand.id().value() : null,
            brand.code().value(),
            brand.canonicalName().value(),
            brand.nameKo(),
            brand.nameEn(),
            brand.shortName(),
            brand.country() != null ? brand.country().code() : null,
            brand.department() != null ? brand.department().name() : null,
            brand.isLuxury(),
            brand.status().name(),
            brand.officialWebsite(),
            brand.logoUrl(),
            brand.description(),
            brand.dataQuality().level().name(),
            brand.dataQuality().score(),
            brand.aliasCount(),
            aliasResponses
        );
    }
    
    public BrandSimpleResponse toSimpleResponse(Brand brand) {
        return new BrandSimpleResponse(
            brand.id() != null ? brand.id().value() : null,
            brand.code().value(),
            brand.nameKo(),
            brand.nameEn()
        );
    }
    
    public BrandAliasResponse toAliasResponse(BrandAlias alias) {
        return new BrandAliasResponse(
            alias.id() != null ? alias.id().value() : null,
            alias.brandId(),
            alias.originalAlias(),
            alias.normalizedAlias(),
            alias.sourceType(),
            alias.sellerId(),
            alias.mallCode(),
            alias.confidenceValue(),
            alias.status().name()
        );
    }
}
