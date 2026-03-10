package com.ryuqq.marketplace.application.outboundproduct.dto.vo;

/**
 * 외부 채널에 등록된 상품 정보.
 *
 * <p>채널별 어댑터가 반환하는 채널-비종속 DTO.
 *
 * @param externalProductId 외부 상품 ID (네이버 originProductNo 등)
 * @param sellerManagementCode 판매자 관리 코드 (레거시 상품그룹 ID와 매핑)
 * @param productName 상품명
 * @param statusType 판매 상태
 */
public record ExternalProductEntry(
        String externalProductId,
        String sellerManagementCode,
        String productName,
        String statusType) {}
