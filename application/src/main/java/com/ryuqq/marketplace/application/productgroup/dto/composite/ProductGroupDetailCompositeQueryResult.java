package com.ryuqq.marketplace.application.productgroup.dto.composite;

import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.time.Instant;

/**
 * 상품 그룹 상세용 Composition 쿼리 결과 DTO.
 *
 * <p>ProductGroup + Seller + Brand + Category + ShippingPolicy + RefundPolicy 크로스 도메인 JOIN 결과입니다.
 *
 * <p>Adapter 레이어에서 단일 쿼리로 기본 정보와 정책 데이터를 함께 조회하여 반환합니다.
 *
 * @param id 상품 그룹 ID
 * @param sellerId 셀러 ID
 * @param sellerName 셀러명
 * @param brandId 브랜드 ID
 * @param brandName 브랜드명
 * @param categoryId 카테고리 ID
 * @param categoryName 카테고리명
 * @param categoryDisplayPath 카테고리 전체 경로 (예: "상의 > 긴팔 > 숏츠")
 * @param productGroupName 상품 그룹명
 * @param optionType 옵션 유형
 * @param status 상품 그룹 상태
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @param shippingPolicy 배송 정책 상세
 * @param refundPolicy 환불 정책 상세
 */
public record ProductGroupDetailCompositeQueryResult(
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
        ShippingPolicyResult shippingPolicy,
        RefundPolicyResult refundPolicy) {}
