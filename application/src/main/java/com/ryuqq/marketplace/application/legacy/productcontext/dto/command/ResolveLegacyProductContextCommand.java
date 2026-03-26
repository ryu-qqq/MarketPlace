package com.ryuqq.marketplace.application.legacy.productcontext.dto.command;

/**
 * 레거시 상품 컨텍스트 리졸빙 Command.
 *
 * @param legacySellerId 레거시 셀러 ID
 * @param legacyBrandId 레거시 브랜드 ID
 * @param legacyCategoryId 레거시 카테고리 ID
 * @param deliveryData 레거시 배송 데이터
 * @param refundData 레거시 환불 데이터
 */
public record ResolveLegacyProductContextCommand(
        long legacySellerId,
        long legacyBrandId,
        long legacyCategoryId,
        LegacyDeliveryData deliveryData,
        LegacyRefundData refundData) {}
