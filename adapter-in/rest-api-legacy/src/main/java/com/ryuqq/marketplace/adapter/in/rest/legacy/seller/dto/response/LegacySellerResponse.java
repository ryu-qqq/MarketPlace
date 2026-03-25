package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response;

/**
 * 세토프 셀러 정보 호환 응답 DTO.
 *
 * <p>GET /seller - 현재 인증된 셀러(admin) 정보 조회. 세토프 레거시 어드민과 동일한 응답 구조.
 */
public record LegacySellerResponse(
        long sellerId, String email, String passwordHash, String roleType, String approvalStatus) {}
