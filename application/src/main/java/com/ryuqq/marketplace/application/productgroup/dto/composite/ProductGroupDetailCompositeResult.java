package com.ryuqq.marketplace.application.productgroup.dto.composite;

import com.ryuqq.marketplace.application.productgroup.dto.response.ProductOptionMatrixResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.ProductGroupDescriptionResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.time.Instant;
import java.util.List;

/**
 * 상품 그룹 상세 Composite 결과 DTO.
 *
 * <p>상품 그룹 기본 정보 + 크로스 도메인 정보 + 이미지 + 옵션-상품 매트릭스 + 배송/환불 정책 + 상세설명 + 고시정보를 통합 제공합니다.
 *
 * @param id 상품 그룹 ID
 * @param sellerId 셀러 ID
 * @param sellerName 셀러명
 * @param brandId 브랜드 ID
 * @param brandName 브랜드명
 * @param categoryId 카테고리 ID
 * @param categoryName 카테고리명
 * @param categoryDisplayPath 카테고리 전체 경로
 * @param productGroupName 상품 그룹명
 * @param optionType 옵션 유형
 * @param status 상품 그룹 상태
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @param images 상품 그룹 이미지 목록
 * @param optionProductMatrix 옵션 구조 + 상품(SKU) 통합 매트릭스
 * @param shippingPolicy 배송 정책 상세
 * @param refundPolicy 환불 정책 상세
 * @param description 상품 상세설명 (nullable)
 * @param productNotice 상품 고시정보 (nullable)
 */
public record ProductGroupDetailCompositeResult(
        Long id,
        Long sellerId,
        String sellerName,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryName,
        String categoryDisplayPath,
        String productGroupName,
        String optionType,
        String status,
        Instant createdAt,
        Instant updatedAt,
        List<ProductGroupImageResult> images,
        ProductOptionMatrixResult optionProductMatrix,
        ShippingPolicyResult shippingPolicy,
        RefundPolicyResult refundPolicy,
        ProductGroupDescriptionResult description,
        ProductNoticeResult productNotice) {

    public ProductGroupDetailCompositeResult {
        images = images != null ? List.copyOf(images) : List.of();
    }
}
