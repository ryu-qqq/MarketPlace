package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response;

/**
 * 세토프 셀러 정보 호환 응답 DTO.
 *
 * <p>GET /seller - 현재 인증된 셀러 정보 조회 (토큰 기반)
 */
public record LegacySellerResponse(long sellerId, String sellerName, String bizNo) {}
