package com.ryuqq.marketplace.domain.shippingpolicy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.LeadTime;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingPolicyName;
import java.time.Instant;
import java.time.LocalTime;

/**
 * ShippingPolicy 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ShippingPolicy 관련 객체들을 생성합니다.
 */
public final class ShippingPolicyFixtures {

    private ShippingPolicyFixtures() {}

    // ===== ShippingPolicyName Fixtures =====
    public static ShippingPolicyName policyName(String value) {
        return ShippingPolicyName.of(value);
    }

    public static ShippingPolicyName defaultPolicyName() {
        return ShippingPolicyName.of("기본 배송 정책");
    }

    // ===== LeadTime Fixtures =====
    public static LeadTime defaultLeadTime() {
        return LeadTime.of(1, 3, LocalTime.of(14, 0));
    }

    public static LeadTime sameDayLeadTime() {
        return LeadTime.sameDay(LocalTime.of(12, 0));
    }

    public static LeadTime nextDayLeadTime() {
        return LeadTime.nextDay(LocalTime.of(14, 0));
    }

    public static LeadTime leadTime(int minDays, int maxDays) {
        return LeadTime.of(minDays, maxDays, LocalTime.of(14, 0));
    }

    // ===== ShippingPolicy Aggregate Fixtures =====
    public static ShippingPolicy newFreeShippingPolicy() {
        return ShippingPolicy.forNew(
                CommonVoFixtures.defaultSellerId(),
                ShippingPolicyName.of("무료배송 정책"),
                true,
                ShippingFeeType.FREE,
                Money.zero(),
                null,
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.now());
    }

    public static ShippingPolicy newPaidShippingPolicy() {
        return ShippingPolicy.forNew(
                CommonVoFixtures.defaultSellerId(),
                ShippingPolicyName.of("유료배송 정책"),
                false,
                ShippingFeeType.PAID,
                CommonVoFixtures.defaultBaseFee(),
                null,
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.now());
    }

    public static ShippingPolicy newConditionalFreeShippingPolicy() {
        return ShippingPolicy.forNew(
                CommonVoFixtures.defaultSellerId(),
                ShippingPolicyName.of("조건부 무료배송 정책"),
                false,
                ShippingFeeType.CONDITIONAL_FREE,
                CommonVoFixtures.defaultBaseFee(),
                CommonVoFixtures.defaultFreeThreshold(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.now());
    }

    public static ShippingPolicy newShippingPolicy(SellerId sellerId, ShippingFeeType feeType) {
        Money freeThreshold =
                feeType.isConditionalFree() ? CommonVoFixtures.defaultFreeThreshold() : null;
        return ShippingPolicy.forNew(
                sellerId,
                defaultPolicyName(),
                false,
                feeType,
                CommonVoFixtures.defaultBaseFee(),
                freeThreshold,
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.now());
    }

    public static ShippingPolicy activeShippingPolicy() {
        return ShippingPolicy.reconstitute(
                ShippingPolicyId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                true,
                true,
                ShippingFeeType.CONDITIONAL_FREE,
                CommonVoFixtures.defaultBaseFee(),
                CommonVoFixtures.defaultFreeThreshold(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static ShippingPolicy activeShippingPolicy(Long id, SellerId sellerId) {
        return ShippingPolicy.reconstitute(
                ShippingPolicyId.of(id),
                sellerId,
                defaultPolicyName(),
                true,
                true,
                ShippingFeeType.CONDITIONAL_FREE,
                CommonVoFixtures.defaultBaseFee(),
                CommonVoFixtures.defaultFreeThreshold(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static ShippingPolicy activeNonDefaultShippingPolicy(Long id, SellerId sellerId) {
        return ShippingPolicy.reconstitute(
                ShippingPolicyId.of(id),
                sellerId,
                defaultPolicyName(),
                false,
                true,
                ShippingFeeType.CONDITIONAL_FREE,
                CommonVoFixtures.defaultBaseFee(),
                CommonVoFixtures.defaultFreeThreshold(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static ShippingPolicy inactiveShippingPolicy() {
        return ShippingPolicy.reconstitute(
                ShippingPolicyId.of(2L),
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                false,
                false,
                ShippingFeeType.PAID,
                CommonVoFixtures.defaultBaseFee(),
                null,
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static ShippingPolicy deletedShippingPolicy() {
        Instant deletedAt = CommonVoFixtures.yesterday();
        return ShippingPolicy.reconstitute(
                ShippingPolicyId.of(3L),
                CommonVoFixtures.defaultSellerId(),
                defaultPolicyName(),
                false,
                false,
                ShippingFeeType.PAID,
                CommonVoFixtures.defaultBaseFee(),
                null,
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultExtraFee(),
                CommonVoFixtures.defaultReturnFee(),
                CommonVoFixtures.defaultExchangeFee(),
                defaultLeadTime(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                deletedAt);
    }
}
