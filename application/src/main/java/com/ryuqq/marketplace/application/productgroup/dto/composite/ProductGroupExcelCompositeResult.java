package com.ryuqq.marketplace.application.productgroup.dto.composite;

import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import java.util.List;

/**
 * 상품 그룹 엑셀 다운로드용 풍부한 Composite 결과 DTO.
 *
 * <p>기본 목록 Composite에 상품(SKU), 이미지, 상세설명 CDN URL, 고시정보를 추가로 포함합니다. Assembler에서 {@link
 * ProductGroupExcelBundle}의 데이터를 조합하여 생성합니다.
 *
 * @param base 기본 목록 Composite (ProductGroup + Brand + Category + Seller + 가격/옵션 enrichment 적용 완료)
 * @param images 상품 그룹 이미지 목록
 * @param products 상품(SKU) 목록 (기본 정보 + 옵션 매핑)
 * @param descriptionCdnUrl 상세설명 CDN URL (null 가능)
 * @param notice 고시정보 (null 가능)
 */
public record ProductGroupExcelCompositeResult(
        ProductGroupListCompositeResult base,
        List<ProductGroupImageResult> images,
        List<ProductResult> products,
        String descriptionCdnUrl,
        ProductNoticeResult notice) {

    public ProductGroupExcelCompositeResult {
        images = images != null ? List.copyOf(images) : List.of();
        products = products != null ? List.copyOf(products) : List.of();
    }
}
