package com.ryuqq.marketplace.domain.shippingpolicy.aggregate;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.CannotDeactivateDefaultShippingPolicyException;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.InvalidFreeThresholdException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.LeadTime;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingPolicyName;
import java.time.Instant;
import java.time.LocalTime;

/**
 * 배송 정책 Aggregate.
 *
 * <h2>비즈니스 규칙</h2>
 *
 * <ul>
 *   <li><b>POL-DEF-001</b>: 셀러당 기본 정책은 정확히 1개만 존재해야 함
 *   <li><b>POL-DEF-002</b>: 기본 정책은 활성화 상태여야 함
 *   <li><b>POL-DEACT-001</b>: 기본 정책은 비활성화 불가
 *   <li><b>POL-DEACT-002</b>: 마지막 활성 정책은 비활성화 불가
 * </ul>
 *
 * @see <a href="docs/business-rules/POLICY_RULES.md">정책 비즈니스 규칙 문서</a>
 */
public class ShippingPolicy {

    private final ShippingPolicyId id;
    private final SellerId sellerId;
    private ShippingPolicyName policyName;
    private boolean defaultPolicy;
    private boolean active;

    private ShippingFeeType shippingFeeType;
    private Money baseFee;
    private Money freeThreshold;

    private Money jejuExtraFee;
    private Money islandExtraFee;

    private Money returnFee;
    private Money exchangeFee;

    private LeadTime leadTime;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private ShippingPolicy(
            ShippingPolicyId id,
            SellerId sellerId,
            ShippingPolicyName policyName,
            boolean defaultPolicy,
            boolean active,
            ShippingFeeType shippingFeeType,
            Money baseFee,
            Money freeThreshold,
            Money jejuExtraFee,
            Money islandExtraFee,
            Money returnFee,
            Money exchangeFee,
            LeadTime leadTime,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.policyName = policyName;
        this.defaultPolicy = defaultPolicy;
        this.active = active;
        this.shippingFeeType = shippingFeeType;
        this.baseFee = baseFee;
        this.freeThreshold = freeThreshold;
        this.jejuExtraFee = jejuExtraFee;
        this.islandExtraFee = islandExtraFee;
        this.returnFee = returnFee;
        this.exchangeFee = exchangeFee;
        this.leadTime = leadTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static ShippingPolicy forNew(
            SellerId sellerId,
            ShippingPolicyName policyName,
            boolean defaultPolicy,
            ShippingFeeType shippingFeeType,
            Money baseFee,
            Money freeThreshold,
            Money jejuExtraFee,
            Money islandExtraFee,
            Money returnFee,
            Money exchangeFee,
            LeadTime leadTime,
            Instant now) {
        validateFeeSettings(shippingFeeType, freeThreshold);

        return new ShippingPolicy(
                ShippingPolicyId.forNew(),
                sellerId,
                policyName,
                defaultPolicy,
                true,
                shippingFeeType,
                baseFee,
                freeThreshold,
                jejuExtraFee,
                islandExtraFee,
                returnFee,
                exchangeFee,
                leadTime,
                now,
                now,
                null);
    }

    public static ShippingPolicy reconstitute(
            ShippingPolicyId id,
            SellerId sellerId,
            ShippingPolicyName policyName,
            boolean defaultPolicy,
            boolean active,
            ShippingFeeType shippingFeeType,
            Money baseFee,
            Money freeThreshold,
            Money jejuExtraFee,
            Money islandExtraFee,
            Money returnFee,
            Money exchangeFee,
            LeadTime leadTime,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ShippingPolicy(
                id,
                sellerId,
                policyName,
                defaultPolicy,
                active,
                shippingFeeType,
                baseFee,
                freeThreshold,
                jejuExtraFee,
                islandExtraFee,
                returnFee,
                exchangeFee,
                leadTime,
                createdAt,
                updatedAt,
                deletedAt);
    }

    private static void validateFeeSettings(ShippingFeeType feeType, Money freeThreshold) {
        if (feeType.isConditionalFree() && (freeThreshold == null || freeThreshold.isZero())) {
            throw new InvalidFreeThresholdException();
        }
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void updatePolicy(
            ShippingPolicyName policyName,
            ShippingFeeType shippingFeeType,
            Money baseFee,
            Money freeThreshold,
            Money jejuExtraFee,
            Money islandExtraFee,
            Money returnFee,
            Money exchangeFee,
            LeadTime leadTime,
            Instant now) {
        validateFeeSettings(shippingFeeType, freeThreshold);

        this.policyName = policyName;
        this.shippingFeeType = shippingFeeType;
        this.baseFee = baseFee;
        this.freeThreshold = freeThreshold;
        this.jejuExtraFee = jejuExtraFee;
        this.islandExtraFee = islandExtraFee;
        this.returnFee = returnFee;
        this.exchangeFee = exchangeFee;
        this.leadTime = leadTime;
        this.updatedAt = now;
    }

    /**
     * UpdateData를 사용한 정책 수정.
     *
     * @param updateData 수정 데이터
     * @param now 수정 시간
     */
    public void update(ShippingPolicyUpdateData updateData, Instant now) {
        updatePolicy(
                updateData.policyName(),
                updateData.shippingFeeType(),
                updateData.baseFee(),
                updateData.freeThreshold(),
                updateData.jejuExtraFee(),
                updateData.islandExtraFee(),
                updateData.returnFee(),
                updateData.exchangeFee(),
                updateData.leadTime(),
                now);
    }

    public void markAsDefault(Instant now) {
        this.defaultPolicy = true;
        this.updatedAt = now;
    }

    public void unmarkDefault(Instant now) {
        this.defaultPolicy = false;
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        this.active = true;
        this.updatedAt = now;
    }

    /**
     * 정책 비활성화.
     *
     * <p><b>POL-DEACT-001</b>: 기본 정책은 비활성화 불가
     *
     * @param now 변경 시간
     * @throws CannotDeactivateDefaultShippingPolicyException 기본 정책인 경우
     */
    public void deactivate(Instant now) {
        if (this.defaultPolicy) {
            throw new CannotDeactivateDefaultShippingPolicyException();
        }
        this.active = false;
        this.updatedAt = now;
    }

    public Money calculateShippingFee(Money orderAmount) {
        return switch (shippingFeeType) {
            case FREE -> Money.zero();
            case PAID -> baseFee;
            case CONDITIONAL_FREE -> {
                if (orderAmount.isGreaterThanOrEqual(freeThreshold)) {
                    yield Money.zero();
                }
                yield baseFee;
            }
            case QUANTITY_BASED -> baseFee;
        };
    }

    public Money calculateShippingFeeWithJeju(Money orderAmount) {
        return calculateShippingFee(orderAmount).add(jejuExtraFee);
    }

    public Money calculateShippingFeeWithIsland(Money orderAmount) {
        return calculateShippingFee(orderAmount).add(islandExtraFee);
    }

    // VO Getters
    public ShippingPolicyId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId.value();
    }

    public ShippingPolicyName policyName() {
        return policyName;
    }

    public String policyNameValue() {
        return policyName.value();
    }

    public boolean isDefaultPolicy() {
        return defaultPolicy;
    }

    public boolean isActive() {
        return active;
    }

    public ShippingFeeType shippingFeeType() {
        return shippingFeeType;
    }

    public Money baseFee() {
        return baseFee;
    }

    public Integer baseFeeValue() {
        return baseFee != null ? baseFee.value() : null;
    }

    public Money freeThreshold() {
        return freeThreshold;
    }

    public Integer freeThresholdValue() {
        return freeThreshold != null ? freeThreshold.value() : null;
    }

    public Money jejuExtraFee() {
        return jejuExtraFee;
    }

    public Integer jejuExtraFeeValue() {
        return jejuExtraFee != null ? jejuExtraFee.value() : null;
    }

    public Money islandExtraFee() {
        return islandExtraFee;
    }

    public Integer islandExtraFeeValue() {
        return islandExtraFee != null ? islandExtraFee.value() : null;
    }

    public Money returnFee() {
        return returnFee;
    }

    public Integer returnFeeValue() {
        return returnFee != null ? returnFee.value() : null;
    }

    public Money exchangeFee() {
        return exchangeFee;
    }

    public Integer exchangeFeeValue() {
        return exchangeFee != null ? exchangeFee.value() : null;
    }

    public LeadTime leadTime() {
        return leadTime;
    }

    public int leadTimeMinDays() {
        return leadTime != null ? leadTime.minDays() : 0;
    }

    public int leadTimeMaxDays() {
        return leadTime != null ? leadTime.maxDays() : 0;
    }

    public LocalTime leadTimeCutoffTime() {
        return leadTime != null ? leadTime.cutoffTime() : null;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant deletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
