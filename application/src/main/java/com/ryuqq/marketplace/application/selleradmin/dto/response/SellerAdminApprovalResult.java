package com.ryuqq.marketplace.application.selleradmin.dto.response;

import java.time.Instant;

/**
 * 셀러 관리자 가입 신청 승인 Result.
 *
 * @param sellerAdminId 셀러 관리자 ID
 * @param authUserId 인증 서버에서 발급된 사용자 ID
 * @param status 변경된 상태 (ACTIVE)
 * @param approvedAt 승인 일시
 * @author ryu-qqq
 * @since 1.1.0
 */
public record SellerAdminApprovalResult(
        String sellerAdminId, String authUserId, String status, Instant approvedAt) {}
