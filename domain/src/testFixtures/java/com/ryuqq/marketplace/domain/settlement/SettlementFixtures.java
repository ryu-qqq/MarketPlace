package com.ryuqq.marketplace.domain.settlement;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.HoldInfo;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementPeriod;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.LocalDate;

/** Settlement 도메인 테스트 Fixtures. */
public final class SettlementFixtures {

    private SettlementFixtures() {}

    private static final String DEFAULT_SETTLEMENT_ID = "01900000-0000-7000-8000-000000000001";
    private static final long DEFAULT_SELLER_ID = 1L;
    private static final String DEFAULT_HOLD_REASON = "이상 거래 의심으로 인한 보류";

    public static SettlementId defaultSettlementId() {
        return SettlementId.of(DEFAULT_SETTLEMENT_ID);
    }

    public static SettlementPeriod defaultPeriod() {
        return SettlementPeriod.of(
                LocalDate.now().minusDays(7), LocalDate.now(), SettlementCycle.WEEKLY);
    }

    public static SettlementAmounts defaultSettlementAmounts() {
        return SettlementAmounts.of(
                Money.of(100000), Money.of(10000), Money.of(5000), Money.of(85000));
    }

    public static HoldInfo defaultHoldInfo() {
        return HoldInfo.of(DEFAULT_HOLD_REASON, CommonVoFixtures.now());
    }

    public static Settlement newSettlement() {
        return Settlement.forNew(
                defaultSettlementId(),
                DEFAULT_SELLER_ID,
                defaultPeriod(),
                defaultSettlementAmounts(),
                5,
                LocalDate.now().plusDays(14),
                CommonVoFixtures.now());
    }

    public static Settlement calculatingSettlement() {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_SELLER_ID,
                SettlementStatus.CALCULATING,
                defaultPeriod(),
                defaultSettlementAmounts(),
                5,
                null,
                LocalDate.now().plusDays(14),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Settlement confirmedSettlement() {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_SELLER_ID,
                SettlementStatus.CONFIRMED,
                defaultPeriod(),
                defaultSettlementAmounts(),
                5,
                null,
                LocalDate.now().plusDays(14),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Settlement completedSettlement() {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_SELLER_ID,
                SettlementStatus.COMPLETED,
                defaultPeriod(),
                defaultSettlementAmounts(),
                5,
                null,
                LocalDate.now().plusDays(14),
                LocalDate.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Settlement heldSettlement() {
        return Settlement.reconstitute(
                defaultSettlementId(),
                DEFAULT_SELLER_ID,
                SettlementStatus.HOLD,
                defaultPeriod(),
                defaultSettlementAmounts(),
                5,
                defaultHoldInfo(),
                LocalDate.now().plusDays(14),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }
}
