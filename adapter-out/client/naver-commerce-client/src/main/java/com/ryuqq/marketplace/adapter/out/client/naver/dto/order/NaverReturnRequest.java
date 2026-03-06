package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 반품 요청.
 *
 * <p>POST .../{productOrderId}/claim/return/request 요청 본문.
 *
 * @param returnReason 반품 사유 (INTENT_CHANGED, BROKEN, WRONG_DELIVERY 등)
 * @param collectDeliveryMethod 수거 배송방법 (DELIVERY, RETURN_INDIVIDUAL 등)
 * @param collectDeliveryCompany 수거 택배사 코드 (CJGLS, HANJIN 등)
 * @param collectTrackingNumber 수거 운송장번호
 * @param returnQuantity 반품 수량 (미입력 시 전체 수량 반품)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverReturnRequest(
        String returnReason,
        String collectDeliveryMethod,
        String collectDeliveryCompany,
        String collectTrackingNumber,
        Integer returnQuantity) {}
