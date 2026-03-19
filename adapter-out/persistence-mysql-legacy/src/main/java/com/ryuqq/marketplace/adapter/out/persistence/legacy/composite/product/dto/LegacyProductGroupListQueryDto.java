package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto;

import java.time.LocalDateTime;

/**
 * 레거시 상품그룹 목록 조회 Phase 2 flat projection DTO.
 *
 * <p>Phase 2에서 상품그룹 + 판매자 + 브랜드 + 카테고리 + 배송정책 + 메인이미지를 JOIN하여 조회한 결과 행입니다. 단건 상세의 {@link
 * LegacyProductGroupBasicQueryDto}와 달리 상품고지, 설명 등은 포함하지 않습니다.
 *
 * @param productGroupId 상품그룹 ID
 * @param productGroupName 상품그룹명
 * @param sellerId 판매자 ID
 * @param sellerName 판매자명
 * @param brandId 브랜드 ID
 * @param brandName 브랜드명
 * @param categoryId 카테고리 ID
 * @param categoryPath 카테고리 경로 ("500,200,100" 형태)
 * @param optionType 옵션 타입
 * @param managementType 관리유형
 * @param regularPrice 정상가
 * @param currentPrice 현재가
 * @param salePrice 판매가
 * @param directDiscountPrice 직접할인금액
 * @param directDiscountRate 직접할인율
 * @param discountRate 할인율
 * @param soldOutYn 품절 여부 (Y/N)
 * @param displayYn 노출 여부 (Y/N)
 * @param productCondition 상품 상태
 * @param origin 원산지
 * @param styleCode 스타일 코드
 * @param insertOperator 등록자
 * @param updateOperator 수정자
 * @param insertDate 등록일
 * @param updateDate 수정일
 * @param mainImageUrl 메인 이미지 URL (MAIN 타입 이미지)
 */
public record LegacyProductGroupListQueryDto(
        long productGroupId,
        String productGroupName,
        long sellerId,
        String sellerName,
        long brandId,
        String brandName,
        long categoryId,
        String categoryPath,
        String optionType,
        String managementType,
        long regularPrice,
        long currentPrice,
        long salePrice,
        long directDiscountPrice,
        int directDiscountRate,
        int discountRate,
        String soldOutYn,
        String displayYn,
        String productCondition,
        String origin,
        String styleCode,
        String insertOperator,
        String updateOperator,
        LocalDateTime insertDate,
        LocalDateTime updateDate,
        String mainImageUrl) {}
