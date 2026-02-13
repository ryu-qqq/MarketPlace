package com.ryuqq.marketplace.domain.order.vo;

/**
 * 외부몰이 제공한 상품 정보. 고객이 주문 시점에 실제로 본 상품 정보이며, 주문의 진실입니다.
 *
 * <p>우리 시스템 데이터가 폴링 시점에 변경되어 있더라도, 이 정보는 외부몰 기준으로 정확합니다.
 */
public record ExternalProductSnapshot(
        String externalProductId,
        String externalOptionId,
        String externalProductName,
        String externalOptionName,
        String externalImageUrl) {

    public ExternalProductSnapshot {
        if (externalProductId == null || externalProductId.isBlank()) {
            throw new IllegalArgumentException("외부 상품 ID는 필수입니다");
        }
    }

    public static ExternalProductSnapshot of(
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl) {
        return new ExternalProductSnapshot(
                externalProductId,
                externalOptionId,
                externalProductName,
                externalOptionName,
                externalImageUrl);
    }
}
