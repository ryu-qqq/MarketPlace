package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 교환 재배송 요청.
 *
 * <p>POST .../{productOrderId}/claim/exchange/re-delivery 요청 본문.
 *
 * @param deliveryMethod 배송방법
 * @param deliveryCompanyCode 택배사 코드
 * @param trackingNumber 운송장번호
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverExchangeReDeliveryRequest(
        String deliveryMethod, String deliveryCompanyCode, String trackingNumber) {}
