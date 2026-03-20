package com.ryuqq.marketplace.adapter.in.rest.legacy.common.security;

/**
 * 레거시 JWT 인증 컨텍스트.
 *
 * <p>JWT claims에서 추출한 인증 정보를 담습니다.
 *
 * @param sellerId 셀러 ID (JWT claim: sellerId)
 * @param email 관리자 이메일 (JWT subject)
 * @param roleType 역할 (JWT claim: role — MASTER, SELLER)
 */
public record LegacyAuthContext(long sellerId, String email, String roleType) {}
