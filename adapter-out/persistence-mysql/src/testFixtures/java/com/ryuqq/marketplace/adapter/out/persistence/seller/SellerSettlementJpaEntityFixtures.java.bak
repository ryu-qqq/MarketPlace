package com.ryuqq.marketplace.adapter.out.persistence.seller;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import java.time.Instant;

/**
 * SellerSettlementJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerSettlementJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerSettlementJpaEntityFixtures {

    private SellerSettlementJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_BANK_CODE = "004";
    public static final String DEFAULT_BANK_NAME = "KB국민은행";
    public static final String DEFAULT_ACCOUNT_NUMBER = "12345678901234";
    public static final String DEFAULT_ACCOUNT_HOLDER_NAME = "홍길동";
    public static final SellerSettlementJpaEntity.SettlementCycleJpaValue DEFAULT_SETTLEMENT_CYCLE =
            SellerSettlementJpaEntity.SettlementCycleJpaValue.MONTHLY;
    public static final Integer DEFAULT_SETTLEMENT_DAY = 25;

    // ===== Entity Fixtures =====

    /** 검증된 상태의 정산 정보 Entity 생성. */
    public static SellerSettlementJpaEntity verifiedEntity() {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                true,
                now,
                now,
                now,
                null);
    }

    /** ID를 지정한 검증된 상태 정산 정보 Entity 생성. */
    public static SellerSettlementJpaEntity verifiedEntity(Long id) {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                true,
                now,
                now,
                now,
                null);
    }

    /** 셀러 ID를 지정한 검증된 상태 정산 정보 Entity 생성. */
    public static SellerSettlementJpaEntity verifiedEntityWithSellerId(Long sellerId) {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                null,
                sellerId,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                true,
                now,
                now,
                now,
                null);
    }

    /** 미검증 상태 정산 정보 Entity 생성. */
    public static SellerSettlementJpaEntity unverifiedEntity() {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                false,
                null,
                now,
                now,
                null);
    }

    /** 삭제된 상태 정산 정보 Entity 생성. */
    public static SellerSettlementJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                false,
                null,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SellerSettlementJpaEntity newEntity() {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                false,
                null,
                now,
                now,
                null);
    }

    /** 주간 정산 주기 Entity 생성. */
    public static SellerSettlementJpaEntity weeklyEntity() {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                SellerSettlementJpaEntity.SettlementCycleJpaValue.WEEKLY,
                1,
                true,
                now,
                now,
                now,
                null);
    }

    /** 격주 정산 주기 Entity 생성. */
    public static SellerSettlementJpaEntity biweeklyEntity() {
        Instant now = Instant.now();
        return SellerSettlementJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                SellerSettlementJpaEntity.SettlementCycleJpaValue.BIWEEKLY,
                15,
                true,
                now,
                now,
                now,
                null);
    }
}
