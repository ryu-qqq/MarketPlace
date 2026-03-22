package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 세토프 호환 QnA 대상 응답 DTO.
 *
 * <p>상품 QnA의 경우 productGroupId, productGroupName, productGroupMainImageUrl, brandName만 사용하고, 주문
 * QnA의 경우 paymentId, orderId, option 필드도 함께 사용합니다.
 */
public record LegacyQnaTargetResponse(
        long productGroupId,
        String productGroupName,
        String productGroupMainImageUrl,
        String brandName,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long paymentId,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long orderId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String option) {

    /** 상품 QnA 대상 생성. */
    public static LegacyQnaTargetResponse product(
            long productGroupId,
            String productGroupName,
            String productGroupMainImageUrl,
            String brandName) {
        return new LegacyQnaTargetResponse(
                productGroupId,
                productGroupName,
                productGroupMainImageUrl,
                brandName,
                null,
                null,
                null);
    }

    /** 주문 QnA 대상 생성. */
    public static LegacyQnaTargetResponse order(
            long productGroupId,
            String productGroupName,
            String productGroupMainImageUrl,
            String brandName,
            long paymentId,
            long orderId,
            String option) {
        return new LegacyQnaTargetResponse(
                productGroupId,
                productGroupName,
                productGroupMainImageUrl,
                brandName,
                paymentId,
                orderId,
                option);
    }
}
