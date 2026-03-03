package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 네이버 커머스 반품 승인 요청.
 *
 * <p>POST .../{productOrderId}/claim/return/approve 요청 본문.
 *
 * @param returnApproveReason 반품 승인 사유
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NaverReturnApproveRequest(String returnApproveReason) {}
