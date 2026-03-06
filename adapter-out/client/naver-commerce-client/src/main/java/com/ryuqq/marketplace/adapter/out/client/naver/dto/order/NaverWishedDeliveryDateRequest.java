package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 배송 희망일 변경 요청.
 *
 * <p>POST /v1/pay-order/seller/product-orders/{productOrderId}/hope-delivery/change 요청 본문.
 *
 * @param hopeDeliveryYmd 배송 희망일 (yyyyMMdd 형식)
 * @param hopeDeliveryHm 배송 희망 시간 (HHmm 형식)
 * @param region 지역 (1~30자)
 * @param changeReason 변경 사유 (1~300자)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverWishedDeliveryDateRequest(
        String hopeDeliveryYmd, String hopeDeliveryHm, String region, String changeReason) {}
