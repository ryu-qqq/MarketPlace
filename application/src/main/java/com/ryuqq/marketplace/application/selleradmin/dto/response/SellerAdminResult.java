package com.ryuqq.marketplace.application.selleradmin.dto.response;

import java.time.LocalDateTime;

/**
 * 셀러 관리자 상세 Result.
 *
 * <p>승인된 관리자 정보를 반환합니다.
 *
 * @param sellerAdminId 셀러 관리자 ID (UUIDv7)
 * @param sellerId 셀러 ID
 * @param loginId 로그인 ID
 * @param name 관리자 이름
 * @param phoneNumber 연락처
 * @param status 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @param authUserId AuthHub 사용자 ID
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author ryu-qqq
 * @since 1.1.0
 */
public record SellerAdminResult(
        String sellerAdminId,
        Long sellerId,
        String loginId,
        String name,
        String phoneNumber,
        String status,
        String authUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
