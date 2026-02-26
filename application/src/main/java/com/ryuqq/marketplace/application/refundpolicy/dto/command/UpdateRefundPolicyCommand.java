package com.ryuqq.marketplace.application.refundpolicy.dto.command;

import java.util.List;

/**
 * 환불정책 수정 Command.
 *
 * @param sellerId 셀러 ID
 * @param policyId 정책 ID
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param returnPeriodDays 반품 가능 기간 (일)
 * @param exchangePeriodDays 교환 가능 기간 (일)
 * @param nonReturnableConditions 반품 불가 조건 목록
 * @param partialRefundEnabled 부분 환불 허용 여부
 * @param inspectionRequired 검수 필요 여부
 * @param inspectionPeriodDays 검수 소요 기간 (일)
 * @param additionalInfo 추가 안내 문구
 */
public record UpdateRefundPolicyCommand(
        Long sellerId,
        Long policyId,
        String policyName,
        Boolean defaultPolicy,
        Integer returnPeriodDays,
        Integer exchangePeriodDays,
        List<String> nonReturnableConditions,
        Boolean partialRefundEnabled,
        Boolean inspectionRequired,
        Integer inspectionPeriodDays,
        String additionalInfo) {}
