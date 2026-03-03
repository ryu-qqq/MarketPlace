package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 교환 거부(철회) 요청.
 *
 * <p>POST .../{productOrderId}/claim/exchange/reject 요청 본문.
 *
 * @param rejectExchangeReason 교환 거부 사유
 */
public record NaverExchangeRejectRequest(String rejectExchangeReason) {}
