package com.ryuqq.marketplace.domain.sellerapplication;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.vo.Agreement;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.time.Instant;

/**
 * SellerApplication 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 SellerApplication 관련 객체들을 생성합니다.
 */
public final class SellerApplicationFixtures {

    private SellerApplicationFixtures() {}

    // ===== 기본 상수 =====
    private static final Long DEFAULT_APPLICATION_ID = 1L;
    private static final Integer DEFAULT_SETTLEMENT_DAY = 15;
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    private static final String DEFAULT_REJECTION_REASON = "서류 미비로 인한 거절";

    // ===== 신규 신청 생성 =====

    public static SellerApplication newApplication() {
        return SellerApplication.apply(
                SellerFixtures.defaultSellerName(),
                SellerFixtures.defaultDisplayName(),
                SellerFixtures.defaultLogoUrl(),
                SellerFixtures.defaultDescription(),
                SellerFixtures.defaultRegistrationNumber(),
                SellerFixtures.defaultCompanyName(),
                SellerFixtures.defaultRepresentative(),
                SellerFixtures.defaultSaleReportNumber(),
                SellerFixtures.defaultBusinessAddress(),
                SellerFixtures.defaultCsContact(),
                SellerFixtures.defaultContactInfo(),
                SellerFixtures.defaultBankAccount(),
                SettlementCycle.MONTHLY,
                DEFAULT_SETTLEMENT_DAY,
                CommonVoFixtures.now());
    }

    // ===== reconstitute - 상태별 =====

    public static SellerApplication pendingApplication(Long id) {
        Instant appliedAt = CommonVoFixtures.yesterday();
        return SellerApplication.reconstitute(
                SellerApplicationId.of(id),
                SellerFixtures.defaultSellerName(),
                SellerFixtures.defaultDisplayName(),
                SellerFixtures.defaultLogoUrl(),
                SellerFixtures.defaultDescription(),
                SellerFixtures.defaultRegistrationNumber(),
                SellerFixtures.defaultCompanyName(),
                SellerFixtures.defaultRepresentative(),
                SellerFixtures.defaultSaleReportNumber(),
                SellerFixtures.defaultBusinessAddress(),
                SellerFixtures.defaultCsContact(),
                SellerFixtures.defaultContactInfo(),
                SellerFixtures.defaultBankAccount(),
                SettlementCycle.MONTHLY,
                DEFAULT_SETTLEMENT_DAY,
                Agreement.reconstitute(appliedAt),
                ApplicationStatus.PENDING,
                appliedAt,
                null,
                null,
                null,
                null);
    }

    public static SellerApplication pendingApplication() {
        return pendingApplication(DEFAULT_APPLICATION_ID);
    }

    public static SellerApplication approvedApplication(Long id) {
        Instant appliedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return SellerApplication.reconstitute(
                SellerApplicationId.of(id),
                SellerFixtures.defaultSellerName(),
                SellerFixtures.defaultDisplayName(),
                SellerFixtures.defaultLogoUrl(),
                SellerFixtures.defaultDescription(),
                SellerFixtures.defaultRegistrationNumber(),
                SellerFixtures.defaultCompanyName(),
                SellerFixtures.defaultRepresentative(),
                SellerFixtures.defaultSaleReportNumber(),
                SellerFixtures.defaultBusinessAddress(),
                SellerFixtures.defaultCsContact(),
                SellerFixtures.defaultContactInfo(),
                SellerFixtures.defaultBankAccount(),
                SettlementCycle.MONTHLY,
                DEFAULT_SETTLEMENT_DAY,
                Agreement.reconstitute(appliedAt),
                ApplicationStatus.APPROVED,
                appliedAt,
                processedAt,
                DEFAULT_PROCESSED_BY,
                null,
                SellerId.of(100L));
    }

    public static SellerApplication approvedApplication() {
        return approvedApplication(DEFAULT_APPLICATION_ID);
    }

    public static SellerApplication rejectedApplication(Long id) {
        Instant appliedAt = CommonVoFixtures.yesterday();
        Instant processedAt = CommonVoFixtures.now();
        return SellerApplication.reconstitute(
                SellerApplicationId.of(id),
                SellerFixtures.defaultSellerName(),
                SellerFixtures.defaultDisplayName(),
                SellerFixtures.defaultLogoUrl(),
                SellerFixtures.defaultDescription(),
                SellerFixtures.defaultRegistrationNumber(),
                SellerFixtures.defaultCompanyName(),
                SellerFixtures.defaultRepresentative(),
                SellerFixtures.defaultSaleReportNumber(),
                SellerFixtures.defaultBusinessAddress(),
                SellerFixtures.defaultCsContact(),
                SellerFixtures.defaultContactInfo(),
                SellerFixtures.defaultBankAccount(),
                SettlementCycle.MONTHLY,
                DEFAULT_SETTLEMENT_DAY,
                Agreement.reconstitute(appliedAt),
                ApplicationStatus.REJECTED,
                appliedAt,
                processedAt,
                DEFAULT_PROCESSED_BY,
                DEFAULT_REJECTION_REASON,
                null);
    }

    public static SellerApplication rejectedApplication() {
        return rejectedApplication(DEFAULT_APPLICATION_ID);
    }

    // ===== VO Fixtures =====

    public static Agreement defaultAgreement() {
        return Agreement.agreedAt(CommonVoFixtures.now());
    }

    public static Agreement defaultAgreement(Instant now) {
        return Agreement.agreedAt(now);
    }
}
