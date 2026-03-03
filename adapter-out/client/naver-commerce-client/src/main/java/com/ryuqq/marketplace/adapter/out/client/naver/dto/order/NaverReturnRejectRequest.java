package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 반품 거부(철회) 요청.
 *
 * <p>POST .../{productOrderId}/claim/return/reject 요청 본문.
 *
 * @param rejectReturnReason 반품 거부 사유
 */
public record NaverReturnRejectRequest(String rejectReturnReason) {}
