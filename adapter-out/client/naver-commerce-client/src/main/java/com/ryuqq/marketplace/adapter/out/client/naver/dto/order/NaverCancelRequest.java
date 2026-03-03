package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 취소 요청.
 *
 * <p>POST .../{productOrderId}/claim/cancel/request 요청 본문.
 *
 * @param cancelReason 취소 사유
 * @param cancelReasonType 취소 사유 유형
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverCancelRequest(String cancelReason, String cancelReasonType) {}
