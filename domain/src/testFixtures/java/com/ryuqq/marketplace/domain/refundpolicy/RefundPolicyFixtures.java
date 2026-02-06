package com.ryuqq.marketplace.domain.refundpolicy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import com.ryuqq.marketplace.domain.refundpolicy.vo.RefundPolicyName;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;

/**
 * RefundPolicy 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 RefundPolicy 관련 객체들을 생성합니다.
 */
public final class RefundPolicyFixtures {

    private RefundPolicyFixtures() {}

    // ===== 기본 상수 =====
    public static final int DEFAULT_RETURN_PERIOD_DAYS = 7;
    public static final int DEFAULT_EXCHANGE_PERIOD_DAYS = 14;
    public static final int DEFAULT_INSPECTION_PERIOD_DAYS = 3;

    // ===== RefundPolicyName Fixtures =====
    public static RefundPolicyName policyName(String value) {
        return RefundPolicyName.of(value);
    }

    public static RefundPolicyName defaultPolicyName() {
        return RefundPolicyName.of("기본 환불 정책");
    }

    // ===== NonReturnableCondition Fixtures =====
    public static List<NonReturnableCondition> defaultNonReturnableConditions() {
        return List.of(
                NonReturnableCondition.OPENED_PACKAGING, NonReturnableCondition.USED_PRODUCT);
    }

    public static List<NonReturnableCondition> allNonReturnableConditions() {
        return List.of(NonReturnableCondition.values());
    }

    public static List<NonReturnableCondition> emptyNonReturnableConditions() {
        return List.of();
    }

    // ===== RefundPolicy Aggregate Fixtures =====
    public static RefundPolicy newRefundPolicy() {
        return RefundPolicy.forNew(
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultNonReturnableConditions(),
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                "추가 정보 없음",
                CommonVoFixtures.now());
    }

    public static RefundPolicy newRefundPolicy(SellerId sellerId) {
        return RefundPolicy.forNew(
                sellerId,
                defaultPolicyName(),
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultNonReturnableConditions(),
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                "추가 정보 없음",
                CommonVoFixtures.now());
    }

    public static RefundPolicy newRefundPolicy(int returnPeriodDays, int exchangePeriodDays) {
        return RefundPolicy.forNew(
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                false,
                returnPeriodDays,
                exchangePeriodDays,
                defaultNonReturnableConditions(),
                true,
                false,
                0,
                null,
                CommonVoFixtures.now());
    }

    public static RefundPolicy activeRefundPolicy() {
        return RefundPolicy.reconstitute(
                RefundPolicyId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultNonReturnableConditions(),
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                "추가 정보",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static RefundPolicy activeRefundPolicy(Long id, SellerId sellerId) {
        return RefundPolicy.reconstitute(
                RefundPolicyId.of(id),
                sellerId,
                defaultPolicyName(),
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultNonReturnableConditions(),
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                "추가 정보",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static RefundPolicy activeNonDefaultRefundPolicy(Long id, SellerId sellerId) {
        return RefundPolicy.reconstitute(
                RefundPolicyId.of(id),
                sellerId,
                defaultPolicyName(),
                false,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultNonReturnableConditions(),
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                "추가 정보",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static RefundPolicy inactiveRefundPolicy() {
        return RefundPolicy.reconstitute(
                RefundPolicyId.of(2L),
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                false,
                false,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultNonReturnableConditions(),
                true,
                false,
                0,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static RefundPolicy deletedRefundPolicy() {
        Instant deletedAt = CommonVoFixtures.yesterday();
        return RefundPolicy.reconstitute(
                RefundPolicyId.of(3L),
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                false,
                false,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultNonReturnableConditions(),
                true,
                false,
                0,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                deletedAt);
    }
}
