package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.dto;

/**
 * 레거시 셀러 인증 정보 flat projection DTO.
 *
 * <p>administrators + admin_auth_group + auth_group + seller 4테이블 조인 결과.
 *
 * @param sellerId 셀러 ID (seller.seller_id)
 * @param email 관리자 이메일 (administrators.EMAIL)
 * @param passwordHash BCrypt 해시 (administrators.PASSWORD_HASH)
 * @param authGroupType 역할 (auth_group.AUTH_GROUP_TYPE: MASTER, SELLER)
 * @param approvalStatus 승인 상태 (administrators.APPROVAL_STATUS)
 */
public record LegacySellerAuthQueryDto(
        long sellerId,
        String email,
        String passwordHash,
        String authGroupType,
        String approvalStatus) {}
