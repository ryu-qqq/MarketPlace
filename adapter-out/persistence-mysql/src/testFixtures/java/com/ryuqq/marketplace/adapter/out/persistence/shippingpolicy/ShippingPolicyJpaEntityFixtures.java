package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import java.time.Instant;
import java.time.LocalTime;

/**
 * ShippingPolicyJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ShippingPolicyJpaEntity 관련 객체들을 생성합니다.
 */
public final class ShippingPolicyJpaEntityFixtures {

    private ShippingPolicyJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_POLICY_NAME = "기본 배송 정책";
    public static final String SHIPPING_FEE_TYPE_FREE = "FREE";
    public static final String SHIPPING_FEE_TYPE_PAID = "PAID";
    public static final String SHIPPING_FEE_TYPE_CONDITIONAL_FREE = "CONDITIONAL_FREE";
    public static final Integer DEFAULT_BASE_FEE = 3000;
    public static final Integer DEFAULT_FREE_THRESHOLD = 50000;
    public static final Integer DEFAULT_EXTRA_FEE = 3000;
    public static final Integer DEFAULT_RETURN_FEE = 3000;
    public static final Integer DEFAULT_EXCHANGE_FEE = 6000;
    public static final Integer DEFAULT_LEAD_TIME_MIN_DAYS = 1;
    public static final Integer DEFAULT_LEAD_TIME_MAX_DAYS = 3;
    public static final LocalTime DEFAULT_CUTOFF_TIME = LocalTime.of(14, 0);

    // ===== Entity Fixtures =====

    /** 활성 상태의 조건부 무료배송 정책 Entity 생성. */
    public static ShippingPolicyJpaEntity activeConditionalFreeEntity() {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                SHIPPING_FEE_TYPE_CONDITIONAL_FREE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 배송 정책 Entity 생성. */
    public static ShippingPolicyJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                SHIPPING_FEE_TYPE_CONDITIONAL_FREE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** ID와 셀러 ID를 지정한 활성 상태 배송 정책 Entity 생성. */
    public static ShippingPolicyJpaEntity activeEntity(Long id, Long sellerId) {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                id,
                sellerId,
                DEFAULT_POLICY_NAME,
                true,
                true,
                SHIPPING_FEE_TYPE_CONDITIONAL_FREE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 무료배송 정책 Entity 생성. */
    public static ShippingPolicyJpaEntity freeShippingEntity() {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                "무료배송 정책",
                true,
                true,
                SHIPPING_FEE_TYPE_FREE,
                0,
                null,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 유료배송 정책 Entity 생성. */
    public static ShippingPolicyJpaEntity paidShippingEntity() {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                "유료배송 정책",
                false,
                true,
                SHIPPING_FEE_TYPE_PAID,
                DEFAULT_BASE_FEE,
                null,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 비활성 상태 배송 정책 Entity 생성. */
    public static ShippingPolicyJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                false,
                false,
                SHIPPING_FEE_TYPE_PAID,
                DEFAULT_BASE_FEE,
                null,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 삭제된 상태 배송 정책 Entity 생성. */
    public static ShippingPolicyJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                false,
                false,
                SHIPPING_FEE_TYPE_PAID,
                DEFAULT_BASE_FEE,
                null,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                now);
    }

    /** LeadTime이 없는 Entity 생성. */
    public static ShippingPolicyJpaEntity entityWithoutLeadTime() {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                false,
                true,
                SHIPPING_FEE_TYPE_PAID,
                DEFAULT_BASE_FEE,
                null,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                null,
                null,
                null,
                now,
                now,
                null);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static ShippingPolicyJpaEntity newEntity() {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                SHIPPING_FEE_TYPE_CONDITIONAL_FREE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 새 활성 상태 배송 정책 Entity 생성 (셀러 ID 지정, ID는 null). */
    public static ShippingPolicyJpaEntity newActiveEntity(Long sellerId) {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                true,
                true,
                SHIPPING_FEE_TYPE_CONDITIONAL_FREE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 새 활성 상태 배송 정책 Entity 생성 (셀러 ID와 정책명 지정, ID는 null). */
    public static ShippingPolicyJpaEntity newActiveEntityWithName(
            Long sellerId, String policyName) {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                sellerId,
                policyName,
                false,
                true,
                SHIPPING_FEE_TYPE_CONDITIONAL_FREE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 새 기본 정책(default) Entity 생성 (셀러 ID 지정, ID는 null). */
    public static ShippingPolicyJpaEntity newDefaultEntity(Long sellerId) {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                true,
                true,
                SHIPPING_FEE_TYPE_CONDITIONAL_FREE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 새 비활성 상태 배송 정책 Entity 생성 (셀러 ID 지정, ID는 null). */
    public static ShippingPolicyJpaEntity newInactiveEntity(Long sellerId) {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                false,
                false,
                SHIPPING_FEE_TYPE_PAID,
                DEFAULT_BASE_FEE,
                null,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                null);
    }

    /** 새 삭제된 상태 배송 정책 Entity 생성 (셀러 ID 지정, ID는 null). */
    public static ShippingPolicyJpaEntity newDeletedEntity(Long sellerId) {
        Instant now = Instant.now();
        return ShippingPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                false,
                false,
                SHIPPING_FEE_TYPE_PAID,
                DEFAULT_BASE_FEE,
                null,
                DEFAULT_EXTRA_FEE,
                DEFAULT_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                DEFAULT_LEAD_TIME_MIN_DAYS,
                DEFAULT_LEAD_TIME_MAX_DAYS,
                DEFAULT_CUTOFF_TIME,
                now,
                now,
                now);
    }
}
