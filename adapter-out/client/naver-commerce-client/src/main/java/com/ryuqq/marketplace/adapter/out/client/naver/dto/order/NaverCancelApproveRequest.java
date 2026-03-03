package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 취소 승인 요청.
 *
 * <p>POST .../{productOrderId}/claim/cancel/approve 요청 본문.
 *
 * @param cancelApproveReason 취소 승인 사유
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverCancelApproveRequest(String cancelApproveReason) {}
