package com.ryuqq.marketplace.application.refundpolicy.dto.command;

import java.util.List;

/**
 * 환불정책 상태 변경 Command.
 *
 * @param sellerId 셀러 ID
 * @param policyIds 상태 변경할 정책 ID 목록
 * @param active 변경할 활성화 상태
 */
public record ChangeRefundPolicyStatusCommand(
        Long sellerId, List<Long> policyIds, Boolean active) {}
