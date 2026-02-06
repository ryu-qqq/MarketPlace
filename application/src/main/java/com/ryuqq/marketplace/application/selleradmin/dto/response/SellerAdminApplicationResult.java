package com.ryuqq.marketplace.application.selleradmin.dto.response;

import java.time.Instant;

/**
 * 셀러 관리자 가입 신청 상세 Result.
 *
 * @param sellerAdminId 셀러 관리자 ID
 * @param sellerId 셀러 ID
 * @param loginId 로그인 ID
 * @param name 관리자 이름
 * @param phoneNumber 휴대폰 번호
 * @param status 상태 (PENDING_APPROVAL, ACTIVE, REJECTED 등)
 * @param authUserId 인증 서버 사용자 ID (승인 후 설정)
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author ryu-qqq
 * @since 1.1.0
 */
public record SellerAdminApplicationResult(
        String sellerAdminId,
        Long sellerId,
        String loginId,
        String name,
        String phoneNumber,
        String status,
        String authUserId,
        Instant createdAt,
        Instant updatedAt) {}
