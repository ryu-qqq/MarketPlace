package com.ryuqq.marketplace.application.selleradmin.dto.command;

/**
 * 셀러 관리자 비밀번호 초기화 Command.
 *
 * @param sellerAdminId 비밀번호를 초기화할 셀러 관리자 ID
 * @author ryu-qqq
 * @since 1.1.0
 */
public record ResetSellerAdminPasswordCommand(String sellerAdminId) {}
