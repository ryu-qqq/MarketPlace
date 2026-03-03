package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 반품 요청.
 *
 * <p>POST .../{productOrderId}/claim/return/request 요청 본문.
 *
 * @param returnReason 반품 사유
 * @param returnReasonType 반품 사유 유형
 * @param collectDeliveryMethod 회수 배송 방법
 * @param collectDeliveryCompanyCode 회수 택배사 코드
 * @param collectTrackingNumber 회수 운송장번호
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverReturnRequest(
        String returnReason,
        String returnReasonType,
        String collectDeliveryMethod,
        String collectDeliveryCompanyCode,
        String collectTrackingNumber) {}
