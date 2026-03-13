package com.ryuqq.marketplace.application.productgroup.dto.composite;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 상품 그룹 상세 조회 번들 DTO.
 *
 * <p>ReadFacade에서 조회한 모든 데이터를 묶어 Service로 전달합니다. Service는 이 번들을 Assembler에 넘겨 최종 결과를 조립합니다.
 *
 * @param queryResult Composition 쿼리 결과 (기본 정보 + 정책)
 * @param group ProductGroup Aggregate (이미지, 옵션 구조)
 * @param products 상품 목록
 * @param description 상품 상세설명
 * @param notice 상품 고시정보
 * @param variantsByImageId 이미지 ID별 Variant 목록
 */
public record ProductGroupDetailBundle(
        ProductGroupDetailCompositeQueryResult queryResult,
        ProductGroup group,
        List<Product> products,
        Optional<ProductGroupDescription> description,
        Optional<ProductNotice> notice,
        Map<Long, List<ImageVariantResult>> variantsByImageId) {

    public ProductGroupDetailBundle {
        variantsByImageId = variantsByImageId != null ? Map.copyOf(variantsByImageId) : Map.of();
    }
}
