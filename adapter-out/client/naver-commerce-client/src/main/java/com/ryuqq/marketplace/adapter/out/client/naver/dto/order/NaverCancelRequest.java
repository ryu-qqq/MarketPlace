package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 취소 요청.
 *
 * <p>POST .../{productOrderId}/claim/cancel/request 요청 본문.
 *
 * @param cancelReason 취소 사유 (INTENT_CHANGED, COLOR_AND_SIZE, WRONG_ORDER 등)
 * @param cancelDetailedReason 취소 상세 사유 (500자 제한)
 * @param cancelQuantity 취소 수량 (미입력 시 전체 수량 취소)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverCancelRequest(
        String cancelReason, String cancelDetailedReason, Integer cancelQuantity) {}
