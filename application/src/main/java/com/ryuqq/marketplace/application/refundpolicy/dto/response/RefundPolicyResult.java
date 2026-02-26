package com.ryuqq.marketplace.application.refundpolicy.dto.response;

import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import java.time.Instant;
import java.util.List;

/**
 * RefundPolicyResult - 환불정책 조회 결과 DTO.
 *
 * <p>APP-DTO-001: Application Result는 record 타입 필수.
 *
 * <p>APP-DTO-002: Result는 Domain 객체에서 직접 변환.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RefundPolicyResult(
        Long policyId,
        Long sellerId,
        String policyName,
        boolean defaultPolicy,
        boolean active,
        int returnPeriodDays,
        int exchangePeriodDays,
        List<NonReturnableConditionResult> nonReturnableConditions,
        boolean partialRefundEnabled,
        boolean inspectionRequired,
        int inspectionPeriodDays,
        String additionalInfo,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain → Result 변환.
     *
     * @param domain RefundPolicy 도메인 객체
     * @return RefundPolicyResult
     */
    public static RefundPolicyResult from(RefundPolicy domain) {
        List<NonReturnableConditionResult> conditionResults =
                domain.nonReturnableConditions().stream()
                        .map(RefundPolicyResult::toConditionResult)
                        .toList();

        return new RefundPolicyResult(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.policyNameValue(),
                domain.isDefaultPolicy(),
                domain.isActive(),
                domain.returnPeriodDays(),
                domain.exchangePeriodDays(),
                conditionResults,
                domain.isPartialRefundEnabled(),
                domain.isInspectionRequired(),
                domain.inspectionPeriodDays(),
                domain.additionalInfo(),
                domain.createdAt(),
                domain.updatedAt());
    }

    private static NonReturnableConditionResult toConditionResult(
            NonReturnableCondition condition) {
        return new NonReturnableConditionResult(condition.name(), condition.displayName());
    }
}
