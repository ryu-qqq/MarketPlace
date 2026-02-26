package com.ryuqq.marketplace.domain.refundpolicy.aggregate;

import com.ryuqq.marketplace.domain.refundpolicy.exception.CannotDeactivateDefaultRefundPolicyException;
import com.ryuqq.marketplace.domain.refundpolicy.exception.InvalidExchangePeriodException;
import com.ryuqq.marketplace.domain.refundpolicy.exception.InvalidReturnPeriodException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.vo.NonReturnableCondition;
import com.ryuqq.marketplace.domain.refundpolicy.vo.RefundPolicyName;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 환불 정책 Aggregate.
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
public class RefundPolicy {

    private static final int MIN_PERIOD_DAYS = 1;
    private static final int MAX_PERIOD_DAYS = 90;

    private final RefundPolicyId id;
    private final SellerId sellerId;
    private RefundPolicyName policyName;
    private boolean defaultPolicy;
    private boolean active;

    private int returnPeriodDays;
    private int exchangePeriodDays;

    private List<NonReturnableCondition> nonReturnableConditions;

    private boolean partialRefundEnabled;

    private boolean inspectionRequired;
    private int inspectionPeriodDays;

    private String additionalInfo;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private RefundPolicy(
            RefundPolicyId id,
            SellerId sellerId,
            RefundPolicyName policyName,
            boolean defaultPolicy,
            boolean active,
            int returnPeriodDays,
            int exchangePeriodDays,
            List<NonReturnableCondition> nonReturnableConditions,
            boolean partialRefundEnabled,
            boolean inspectionRequired,
            int inspectionPeriodDays,
            String additionalInfo,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.policyName = policyName;
        this.defaultPolicy = defaultPolicy;
        this.active = active;
        this.returnPeriodDays = returnPeriodDays;
        this.exchangePeriodDays = exchangePeriodDays;
        this.nonReturnableConditions =
                nonReturnableConditions != null
                        ? new ArrayList<>(nonReturnableConditions)
                        : new ArrayList<>();
        this.partialRefundEnabled = partialRefundEnabled;
        this.inspectionRequired = inspectionRequired;
        this.inspectionPeriodDays = inspectionPeriodDays;
        this.additionalInfo = additionalInfo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static RefundPolicy forNew(
            SellerId sellerId,
            RefundPolicyName policyName,
            boolean defaultPolicy,
            int returnPeriodDays,
            int exchangePeriodDays,
            List<NonReturnableCondition> nonReturnableConditions,
            boolean partialRefundEnabled,
            boolean inspectionRequired,
            int inspectionPeriodDays,
            String additionalInfo,
            Instant now) {
        validateReturnPeriod(returnPeriodDays);
        validateExchangePeriod(exchangePeriodDays);

        return new RefundPolicy(
                RefundPolicyId.forNew(),
                sellerId,
                policyName,
                defaultPolicy,
                true,
                returnPeriodDays,
                exchangePeriodDays,
                nonReturnableConditions,
                partialRefundEnabled,
                inspectionRequired,
                inspectionPeriodDays,
                additionalInfo,
                now,
                now,
                null);
    }

    public static RefundPolicy reconstitute(
            RefundPolicyId id,
            SellerId sellerId,
            RefundPolicyName policyName,
            boolean defaultPolicy,
            boolean active,
            int returnPeriodDays,
            int exchangePeriodDays,
            List<NonReturnableCondition> nonReturnableConditions,
            boolean partialRefundEnabled,
            boolean inspectionRequired,
            int inspectionPeriodDays,
            String additionalInfo,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new RefundPolicy(
                id,
                sellerId,
                policyName,
                defaultPolicy,
                active,
                returnPeriodDays,
                exchangePeriodDays,
                nonReturnableConditions,
                partialRefundEnabled,
                inspectionRequired,
                inspectionPeriodDays,
                additionalInfo,
                createdAt,
                updatedAt,
                deletedAt);
    }

    private static void validateReturnPeriod(int days) {
        if (days < MIN_PERIOD_DAYS || days > MAX_PERIOD_DAYS) {
            throw new InvalidReturnPeriodException();
        }
    }

    private static void validateExchangePeriod(int days) {
        if (days < MIN_PERIOD_DAYS || days > MAX_PERIOD_DAYS) {
            throw new InvalidExchangePeriodException();
        }
    }

    public boolean isNew() {
        return id.isNew();
    }

    public void updatePolicy(
            RefundPolicyName policyName,
            int returnPeriodDays,
            int exchangePeriodDays,
            List<NonReturnableCondition> nonReturnableConditions,
            boolean partialRefundEnabled,
            boolean inspectionRequired,
            int inspectionPeriodDays,
            String additionalInfo,
            Instant now) {
        validateReturnPeriod(returnPeriodDays);
        validateExchangePeriod(exchangePeriodDays);

        this.policyName = policyName;
        this.returnPeriodDays = returnPeriodDays;
        this.exchangePeriodDays = exchangePeriodDays;
        this.nonReturnableConditions =
                nonReturnableConditions != null
                        ? new ArrayList<>(nonReturnableConditions)
                        : new ArrayList<>();
        this.partialRefundEnabled = partialRefundEnabled;
        this.inspectionRequired = inspectionRequired;
        this.inspectionPeriodDays = inspectionPeriodDays;
        this.additionalInfo = additionalInfo;
        this.updatedAt = now;
    }

    /**
     * UpdateData를 사용한 정책 수정.
     *
     * @param updateData 수정 데이터
     * @param now 수정 시간
     */
    public void update(RefundPolicyUpdateData updateData, Instant now) {
        updatePolicy(
                updateData.policyName(),
                updateData.returnPeriodDays(),
                updateData.exchangePeriodDays(),
                updateData.nonReturnableConditions(),
                updateData.partialRefundEnabled(),
                updateData.inspectionRequired(),
                updateData.inspectionPeriodDays(),
                updateData.additionalInfo(),
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
     * @throws CannotDeactivateDefaultRefundPolicyException 기본 정책인 경우
     */
    public void deactivate(Instant now) {
        if (this.defaultPolicy) {
            throw new CannotDeactivateDefaultRefundPolicyException();
        }
        this.active = false;
        this.updatedAt = now;
    }

    public boolean isReturnableWithinPeriod(int daysSincePurchase) {
        return daysSincePurchase <= returnPeriodDays;
    }

    public boolean isExchangeableWithinPeriod(int daysSincePurchase) {
        return daysSincePurchase <= exchangePeriodDays;
    }

    public boolean hasNonReturnableCondition(NonReturnableCondition condition) {
        return nonReturnableConditions.contains(condition);
    }

    // VO Getters
    public RefundPolicyId id() {
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

    public RefundPolicyName policyName() {
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

    public int returnPeriodDays() {
        return returnPeriodDays;
    }

    public int exchangePeriodDays() {
        return exchangePeriodDays;
    }

    public List<NonReturnableCondition> nonReturnableConditions() {
        return Collections.unmodifiableList(nonReturnableConditions);
    }

    public boolean isPartialRefundEnabled() {
        return partialRefundEnabled;
    }

    public boolean isInspectionRequired() {
        return inspectionRequired;
    }

    public int inspectionPeriodDays() {
        return inspectionPeriodDays;
    }

    public String additionalInfo() {
        return additionalInfo;
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
