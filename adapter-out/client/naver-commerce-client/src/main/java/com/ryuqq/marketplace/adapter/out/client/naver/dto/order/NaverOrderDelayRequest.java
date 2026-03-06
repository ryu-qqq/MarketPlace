package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 발송 지연 처리 요청.
 *
 * <p>POST /v1/pay-order/seller/product-orders/{productOrderId}/delay 요청 본문.
 *
 * @param dispatchDueDate 발송 기한 (ISO 8601)
 * @param delayedDispatchReason 발송 지연 사유 코드 (PRODUCT_PREPARE, CUSTOMER_REQUEST 등)
 * @param dispatchDelayedDetailedReason 발송 지연 상세 사유
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverOrderDelayRequest(
        String dispatchDueDate,
        String delayedDispatchReason,
        String dispatchDelayedDetailedReason) {}
