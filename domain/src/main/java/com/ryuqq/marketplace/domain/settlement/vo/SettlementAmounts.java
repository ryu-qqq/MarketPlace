package com.ryuqq.marketplace.domain.settlement.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;

/**
 * 정산 집계 금액 정보.
 *
 * <p>Entry 집계 기반: netSettlementAmount = totalSalesAmount - totalCommissionAmount -
 * totalReversalAmount.
 */
public record SettlementAmounts(
        Money totalSalesAmount,
        Money totalCommissionAmount,
        Money totalReversalAmount,
        Money netSettlementAmount) {

    public SettlementAmounts {
        if (totalSalesAmount == null) {
            throw new IllegalArgumentException("총 판매 금액은 null일 수 없습니다");
        }
        if (totalCommissionAmount == null) {
            throw new IllegalArgumentException("총 수수료 금액은 null일 수 없습니다");
        }
        if (totalReversalAmount == null) {
            throw new IllegalArgumentException("총 역분개 금액은 null일 수 없습니다");
        }
        if (netSettlementAmount == null) {
            throw new IllegalArgumentException("순 정산 금액은 null일 수 없습니다");
        }
    }

    /** Entry 집계 결과로 생성. */
    public static SettlementAmounts of(
            Money totalSalesAmount,
            Money totalCommissionAmount,
            Money totalReversalAmount,
            Money netSettlementAmount) {
        return new SettlementAmounts(
                totalSalesAmount, totalCommissionAmount, totalReversalAmount, netSettlementAmount);
    }

    /** 빈 금액으로 생성. */
    public static SettlementAmounts zero() {
        return new SettlementAmounts(Money.zero(), Money.zero(), Money.zero(), Money.zero());
    }
}
