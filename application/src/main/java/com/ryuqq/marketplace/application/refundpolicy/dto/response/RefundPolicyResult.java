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
 * @param policyId 정책 ID
 * @param policyName 정책명
 * @param defaultPolicy 기본 정책 여부
 * @param active 활성화 상태
 * @param returnPeriodDays 반품 가능 기간
 * @param exchangePeriodDays 교환 가능 기간
 * @param nonReturnableConditions 반품 불가 조건 목록
 * @param createdAt 생성일시
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RefundPolicyResult(
        Long policyId,
        String policyName,
        boolean defaultPolicy,
        boolean active,
        int returnPeriodDays,
        int exchangePeriodDays,
        List<NonReturnableConditionResult> nonReturnableConditions,
        Instant createdAt) {

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
                domain.policyNameValue(),
                domain.isDefaultPolicy(),
                domain.isActive(),
                domain.returnPeriodDays(),
                domain.exchangePeriodDays(),
                conditionResults,
                domain.createdAt());
    }

    private static NonReturnableConditionResult toConditionResult(
            NonReturnableCondition condition) {
        return new NonReturnableConditionResult(condition.name(), condition.displayName());
    }
}
