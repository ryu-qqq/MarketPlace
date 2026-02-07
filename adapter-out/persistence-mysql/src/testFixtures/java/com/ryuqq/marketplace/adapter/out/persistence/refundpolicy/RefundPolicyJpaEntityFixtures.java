package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import java.time.Instant;

/**
 * RefundPolicyJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 RefundPolicyJpaEntity 관련 객체들을 생성합니다.
 */
public final class RefundPolicyJpaEntityFixtures {

    private RefundPolicyJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_POLICY_NAME = "기본 환불 정책";
    public static final int DEFAULT_RETURN_PERIOD_DAYS = 7;
    public static final int DEFAULT_EXCHANGE_PERIOD_DAYS = 14;
    public static final String DEFAULT_NON_RETURNABLE_CONDITIONS = "OPENED_PACKAGING,USED_PRODUCT";
    public static final int DEFAULT_INSPECTION_PERIOD_DAYS = 3;
    public static final String DEFAULT_ADDITIONAL_INFO = "추가 정보";

    // ===== Entity Fixtures =====

    /** 활성 상태의 기본 환불 정책 Entity 생성. */
    public static RefundPolicyJpaEntity activeEntity() {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 환불 정책 Entity 생성. */
    public static RefundPolicyJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** ID와 셀러 ID를 지정한 활성 상태 환불 정책 Entity 생성. */
    public static RefundPolicyJpaEntity activeEntity(Long id, Long sellerId) {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                id,
                sellerId,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** 비활성 상태 환불 정책 Entity 생성. */
    public static RefundPolicyJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                false,
                false,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                false,
                0,
                null,
                now,
                now,
                null);
    }

    /** 삭제된 상태 환불 정책 Entity 생성. */
    public static RefundPolicyJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                false,
                false,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                false,
                0,
                null,
                now,
                now,
                now);
    }

    /** 반품 불가 조건이 없는 Entity 생성. */
    public static RefundPolicyJpaEntity entityWithoutConditions() {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                false,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                null,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** 빈 반품 불가 조건 문자열 Entity 생성. */
    public static RefundPolicyJpaEntity entityWithEmptyConditions() {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                false,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                "",
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static RefundPolicyJpaEntity newEntity() {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** 새 활성 상태 환불 정책 Entity 생성 (셀러 ID 지정, ID는 null). */
    public static RefundPolicyJpaEntity newActiveEntity(Long sellerId) {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** 새 활성 상태 환불 정책 Entity 생성 (셀러 ID와 정책명 지정, ID는 null). */
    public static RefundPolicyJpaEntity newActiveEntityWithName(Long sellerId, String policyName) {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                sellerId,
                policyName,
                false,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** 새 기본 정책(default) Entity 생성 (셀러 ID 지정, ID는 null). */
    public static RefundPolicyJpaEntity newDefaultEntity(Long sellerId) {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                true,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO,
                now,
                now,
                null);
    }

    /** 새 비활성 상태 환불 정책 Entity 생성 (셀러 ID 지정, ID는 null). */
    public static RefundPolicyJpaEntity newInactiveEntity(Long sellerId) {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                false,
                false,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                false,
                0,
                null,
                now,
                now,
                null);
    }

    /** 새 삭제된 상태 환불 정책 Entity 생성 (셀러 ID 지정, ID는 null). */
    public static RefundPolicyJpaEntity newDeletedEntity(Long sellerId) {
        Instant now = Instant.now();
        return RefundPolicyJpaEntity.create(
                null,
                sellerId,
                DEFAULT_POLICY_NAME,
                false,
                false,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_NON_RETURNABLE_CONDITIONS,
                true,
                false,
                0,
                null,
                now,
                now,
                now);
    }
}
