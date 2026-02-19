package com.ryuqq.marketplace.domain.settlement;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.DeductionPayer;
import com.ryuqq.marketplace.domain.settlement.vo.DeductionType;
import com.ryuqq.marketplace.domain.settlement.vo.HoldInfo;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementDeduction;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.LocalDate;
import java.util.List;

/**
 * Settlement 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Settlement 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SettlementFixtures {

    private SettlementFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_SETTLEMENT_ID = "01900000-0000-7000-8000-000000000001";
    private static final String DEFAULT_ORDER_ID = "ORDER-20260101-001";
    private static final long DEFAULT_SELLER_ID = 1L;
    private static final String DEFAULT_HOLD_REASON = "이상 거래 의심으로 인한 보류";

    // ===== ID Fixtures =====

    public static SettlementId defaultSettlementId() {
        return SettlementId.of(DEFAULT_SETTLEMENT_ID);
    }

    public static SettlementId settlementId(String value) {
        return SettlementId.of(value);
    }

    // ===== VO Fixtures =====

    public static SettlementDeduction defaultSellerDeduction() {
        return SettlementDeduction.of(
                DeductionType.COUPON, DeductionPayer.SELLER, Money.of(3000), "쿠폰 할인 (셀러 부담)");
    }

    public static SettlementDeduction defaultPlatformDeduction() {
        return SettlementDeduction.of(
                DeductionType.MILEAGE, DeductionPayer.PLATFORM, Money.of(1000), "마일리지 할인 (플랫폼 부담)");
    }

    public static SettlementAmounts defaultSettlementAmounts() {
        return new SettlementAmounts(
                Money.of(100000),
                List.of(defaultSellerDeduction(), defaultPlatformDeduction()),
                Money.of(10000),
                10,
                Money.of(86000),
                Money.of(86000));
    }

    public static SettlementAmounts settlementAmountsWithoutDeductions() {
        return new SettlementAmounts(
                Money.of(50000), List.of(), Money.of(5000), 10, Money.of(45000), Money.of(45000));
    }

    public static HoldInfo defaultHoldInfo() {
        return HoldInfo.of(DEFAULT_HOLD_REASON, CommonVoFixtures.now());
    }

    public static HoldInfo holdInfo(String reason) {
        return HoldInfo.of(reason, CommonVoFixtures.now());
    }

    // ===== forNew Fixtures =====

    public static Settlement newSettlement() {
        return Settlement.forNew(
                defaultSettlementId(),
                DEFAULT_ORDER_ID,
                DEFAULT_SELLER_ID,
                defaultSettlementAmounts(),
                LocalDate.now().plusDays(14),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.now());
    }

    public static Settlement newSettlement(String settlementId, String orderId) {
        return Settlement.forNew(
                SettlementId.of(settlementId),
                orderId,
                DEFAULT_SELLER_ID,
                defaultSettlementAmounts(),
                LocalDate.now().plusDays(14),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.now());
    }

    // ===== reconstitute Fixtures =====

    public static Settlement pendingSettlement() {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_ORDER_ID,
                DEFAULT_SELLER_ID,
                SettlementStatus.PENDING,
                defaultSettlementAmounts(),
                null,
                LocalDate.now().plusDays(14),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Settlement pendingSettlement(String settlementId) {
        return Settlement.reconstitute(
                SettlementId.of(settlementId),
                DEFAULT_ORDER_ID,
                DEFAULT_SELLER_ID,
                SettlementStatus.PENDING,
                defaultSettlementAmounts(),
                null,
                LocalDate.now().plusDays(14),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Settlement completedSettlement() {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_ORDER_ID,
                DEFAULT_SELLER_ID,
                SettlementStatus.COMPLETED,
                defaultSettlementAmounts(),
                null,
                LocalDate.now().plusDays(14),
                LocalDate.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Settlement heldSettlement() {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_ORDER_ID,
                DEFAULT_SELLER_ID,
                SettlementStatus.HOLD,
                defaultSettlementAmounts(),
                defaultHoldInfo(),
                LocalDate.now().plusDays(14),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Settlement heldSettlement(String reason) {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_ORDER_ID,
                DEFAULT_SELLER_ID,
                SettlementStatus.HOLD,
                defaultSettlementAmounts(),
                HoldInfo.of(reason, CommonVoFixtures.now()),
                LocalDate.now().plusDays(14),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }
}
