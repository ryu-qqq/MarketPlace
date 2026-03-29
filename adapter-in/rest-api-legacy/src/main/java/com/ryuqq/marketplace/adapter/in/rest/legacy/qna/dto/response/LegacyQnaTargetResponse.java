package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 세토프 호환 QnA 대상 응답 DTO.
 *
 * <p>상품 QnA의 경우 productGroupId, productGroupName, productGroupMainImageUrl, brand만 사용하고, 주문 QnA의 경우
 * paymentId, orderId, option 필드도 함께 사용합니다.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record LegacyQnaTargetResponse(
        long productGroupId,
        String productGroupName,
        String productGroupMainImageUrl,
        BrandInfo brand,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long paymentId,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long orderId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String option) {

    public record BrandInfo(long brandId, String brandName) {}

    /** 상품 QnA 대상 생성. */
    public static LegacyQnaTargetResponse product(
            long productGroupId,
            String productGroupName,
            String productGroupMainImageUrl,
            long brandId,
            String brandName) {
        return new LegacyQnaTargetResponse(
                productGroupId,
                productGroupName,
                productGroupMainImageUrl,
                new BrandInfo(brandId, brandName),
                null,
                null,
                null);
    }

    /** 주문 QnA 대상 생성. */
    public static LegacyQnaTargetResponse order(
            long productGroupId,
            String productGroupName,
            String productGroupMainImageUrl,
            long brandId,
            String brandName,
            long paymentId,
            long orderId,
            String option) {
        return new LegacyQnaTargetResponse(
                productGroupId,
                productGroupName,
                productGroupMainImageUrl,
                new BrandInfo(brandId, brandName),
                paymentId,
                orderId,
                option);
    }
}
