package com.ryuqq.marketplace.adapter.out.persistence.seller;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * SellerContractJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerContractJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerContractJpaEntityFixtures {

    private SellerContractJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("5.00");
    public static final LocalDate DEFAULT_CONTRACT_START_DATE = LocalDate.of(2024, 1, 1);
    public static final LocalDate DEFAULT_CONTRACT_END_DATE = LocalDate.of(2024, 12, 31);
    public static final SellerContractJpaEntity.ContractStatusJpaValue DEFAULT_STATUS =
            SellerContractJpaEntity.ContractStatusJpaValue.ACTIVE;
    public static final String DEFAULT_SPECIAL_TERMS = "특별 약관 내용";

    // ===== Entity Fixtures =====

    /** 활성 상태의 계약 정보 Entity 생성. */
    public static SellerContractJpaEntity activeEntity() {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                DEFAULT_CONTRACT_END_DATE,
                DEFAULT_STATUS,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 계약 정보 Entity 생성. */
    public static SellerContractJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                DEFAULT_CONTRACT_END_DATE,
                DEFAULT_STATUS,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                null);
    }

    /** 만료된 상태 계약 정보 Entity 생성. */
    public static SellerContractJpaEntity expiredEntity() {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                LocalDate.of(2023, 12, 31),
                SellerContractJpaEntity.ContractStatusJpaValue.EXPIRED,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                null);
    }

    /** 종료된 상태 계약 정보 Entity 생성. */
    public static SellerContractJpaEntity terminatedEntity() {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                DEFAULT_CONTRACT_END_DATE,
                SellerContractJpaEntity.ContractStatusJpaValue.TERMINATED,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                null);
    }

    /** 삭제된 상태 계약 정보 Entity 생성. */
    public static SellerContractJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                DEFAULT_CONTRACT_END_DATE,
                DEFAULT_STATUS,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SellerContractJpaEntity newEntity() {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                DEFAULT_CONTRACT_END_DATE,
                DEFAULT_STATUS,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                null);
    }

    /** 셀러 ID를 지정한 활성 상태 계약 정보 Entity 생성. */
    public static SellerContractJpaEntity activeEntityWithSellerId(Long sellerId) {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                null,
                sellerId,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                DEFAULT_CONTRACT_END_DATE,
                DEFAULT_STATUS,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                null);
    }

    /** 특별 약관이 없는 Entity 생성. */
    public static SellerContractJpaEntity entityWithoutSpecialTerms() {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                DEFAULT_CONTRACT_END_DATE,
                DEFAULT_STATUS,
                null,
                now,
                now,
                null);
    }

    /** 계약 종료일이 없는 Entity 생성. */
    public static SellerContractJpaEntity entityWithoutEndDate() {
        Instant now = Instant.now();
        return SellerContractJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_COMMISSION_RATE,
                DEFAULT_CONTRACT_START_DATE,
                null,
                DEFAULT_STATUS,
                DEFAULT_SPECIAL_TERMS,
                now,
                now,
                null);
    }
}
