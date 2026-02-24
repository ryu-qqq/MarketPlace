package com.ryuqq.marketplace.application.productgroup.dto.composite;

import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import java.util.List;
import java.util.Map;

/**
 * 상품 그룹 엑셀 다운로드용 번들 DTO.
 *
 * <p>ReadFacade에서 조회한 기본 Composite + enrichment + 크로스 도메인 데이터를 묶어 Assembler로 전달합니다.
 *
 * <p>조회 전략:
 *
 * <ul>
 *   <li>1단계: 기본 Composition JOIN 쿼리 (ProductGroup + Brand + Category + Seller)
 *   <li>2단계: productGroupIds 추출 → IN절 배치 조회 (enrichment, images, products, descriptions, notices)
 * </ul>
 *
 * @param baseComposites 기본 Composition 쿼리 결과 목록
 * @param enrichments 가격 + 옵션 통합 enrichment 결과 목록
 * @param imagesByProductGroupId 상품 그룹별 이미지 목록
 * @param productsByProductGroupId 상품 그룹별 상품(SKU) 상세 목록
 * @param descriptionCdnUrlByProductGroupId 상품 그룹별 상세설명 CDN URL
 * @param noticeByProductGroupId 상품 그룹별 고시정보
 * @param totalElements 전체 건수
 */
public record ProductGroupExcelBundle(
        List<ProductGroupListCompositeResult> baseComposites,
        List<ProductGroupEnrichmentResult> enrichments,
        Map<Long, List<ProductGroupImageResult>> imagesByProductGroupId,
        Map<Long, List<ProductResult>> productsByProductGroupId,
        Map<Long, String> descriptionCdnUrlByProductGroupId,
        Map<Long, ProductNoticeResult> noticeByProductGroupId,
        long totalElements) {

    public ProductGroupExcelBundle {
        baseComposites = baseComposites != null ? List.copyOf(baseComposites) : List.of();
        enrichments = enrichments != null ? List.copyOf(enrichments) : List.of();
        imagesByProductGroupId =
                imagesByProductGroupId != null ? Map.copyOf(imagesByProductGroupId) : Map.of();
        productsByProductGroupId =
                productsByProductGroupId != null ? Map.copyOf(productsByProductGroupId) : Map.of();
        descriptionCdnUrlByProductGroupId =
                descriptionCdnUrlByProductGroupId != null
                        ? Map.copyOf(descriptionCdnUrlByProductGroupId)
                        : Map.of();
        noticeByProductGroupId =
                noticeByProductGroupId != null ? Map.copyOf(noticeByProductGroupId) : Map.of();
    }
}
