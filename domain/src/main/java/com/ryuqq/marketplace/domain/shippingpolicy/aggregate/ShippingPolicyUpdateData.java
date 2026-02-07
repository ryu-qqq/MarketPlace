package com.ryuqq.marketplace.domain.shippingpolicy.aggregate;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.LeadTime;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingPolicyName;

/**
 * 배송 정책 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record ShippingPolicyUpdateData(
        ShippingPolicyName policyName,
        ShippingFeeType shippingFeeType,
        Money baseFee,
        Money freeThreshold,
        Money jejuExtraFee,
        Money islandExtraFee,
        Money returnFee,
        Money exchangeFee,
        LeadTime leadTime) {

    public static ShippingPolicyUpdateData of(
            ShippingPolicyName policyName,
            ShippingFeeType shippingFeeType,
            Money baseFee,
            Money freeThreshold,
            Money jejuExtraFee,
            Money islandExtraFee,
            Money returnFee,
            Money exchangeFee,
            LeadTime leadTime) {
        return new ShippingPolicyUpdateData(
                policyName,
                shippingFeeType,
                baseFee,
                freeThreshold,
                jejuExtraFee,
                islandExtraFee,
                returnFee,
                exchangeFee,
                leadTime);
    }
}
