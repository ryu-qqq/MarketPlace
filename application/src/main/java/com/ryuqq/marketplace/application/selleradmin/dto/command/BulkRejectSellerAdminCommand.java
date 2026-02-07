package com.ryuqq.marketplace.application.selleradmin.dto.command;

import java.util.List;

/**
 * 셀러 관리자 가입 신청 일괄 거절 Command.
 *
 * @param sellerAdminIds 거절할 셀러 관리자 ID 목록
 * @author ryu-qqq
 * @since 1.1.0
 */
public record BulkRejectSellerAdminCommand(List<String> sellerAdminIds) {}
