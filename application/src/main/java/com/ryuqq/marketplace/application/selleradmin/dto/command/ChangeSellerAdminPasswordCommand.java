package com.ryuqq.marketplace.application.selleradmin.dto.command;

/**
 * 셀러 관리자 비밀번호 변경 Command.
 *
 * <p>외부 본인인증 완료 후 새 비밀번호만 전달합니다.
 *
 * @param sellerAdminId 비밀번호를 변경할 셀러 관리자 ID
 * @param newPassword 새 비밀번호
 * @author ryu-qqq
 * @since 1.1.0
 */
public record ChangeSellerAdminPasswordCommand(String sellerAdminId, String newPassword) {}
