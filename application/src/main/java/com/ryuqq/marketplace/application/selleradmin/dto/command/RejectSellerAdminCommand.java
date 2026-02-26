package com.ryuqq.marketplace.application.selleradmin.dto.command;

/**
 * 셀러 관리자 가입 신청 거절 Command.
 *
 * @param sellerAdminId 거절할 셀러 관리자 ID
 * @author ryu-qqq
 * @since 1.1.0
 */
public record RejectSellerAdminCommand(String sellerAdminId) {}
