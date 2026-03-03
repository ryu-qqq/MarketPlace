package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 교환 재배송 요청.
 *
 * <p>POST .../{productOrderId}/claim/exchange/dispatch 요청 본문.
 *
 * @param reDeliveryMethod 재배송 방법 (DELIVERY 등)
 * @param reDeliveryCompany 재배송 택배사 코드
 * @param reDeliveryTrackingNumber 재배송 운송장번호
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverExchangeReDeliveryRequest(
        String reDeliveryMethod, String reDeliveryCompany, String reDeliveryTrackingNumber) {}
