package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 반품/교환 거절 요청 (공용).
 *
 * <p>반품 거절 및 교환 거절 API에 공통으로 사용.
 *
 * @param rejectReason 거절 사유
 */
public record NaverClaimRejectRequest(String rejectReason) {}
