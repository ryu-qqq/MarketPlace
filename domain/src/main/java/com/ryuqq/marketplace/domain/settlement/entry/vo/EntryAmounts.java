package com.ryuqq.marketplace.domain.settlement.entry.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;

/**
 * 정산 원장 금액 정보.
 *
 * <p>commissionRate는 basis point (1% = 100). settlementAmount = salesAmount - commissionAmount.
 */
public record EntryAmounts(
        Money salesAmount, int commissionRate, Money commissionAmount, Money settlementAmount) {

    public EntryAmounts {
        if (salesAmount == null) {
            throw new IllegalArgumentException("salesAmount는 null일 수 없습니다");
        }
        if (commissionRate < 0) {
            throw new IllegalArgumentException("commissionRate는 0 이상이어야 합니다");
        }
        if (commissionAmount == null) {
            throw new IllegalArgumentException("commissionAmount는 null일 수 없습니다");
        }
        if (settlementAmount == null) {
            throw new IllegalArgumentException("settlementAmount는 null일 수 없습니다");
        }
    }

    /**
     * 판매금액과 수수료율로 금액을 계산합니다.
     *
     * @param salesAmount 판매 금액
     * @param commissionRate 수수료율 (basis point, 1% = 100)
     * @return 계산된 EntryAmounts
     */
    public static EntryAmounts calculate(Money salesAmount, int commissionRate) {
        if (salesAmount == null) {
            throw new IllegalArgumentException("salesAmount는 null일 수 없습니다");
        }
        int commissionValue = salesAmount.value() * commissionRate / 10000;
        Money commissionAmount = Money.of(commissionValue);
        Money settlementAmount = salesAmount.subtract(commissionAmount);
        return new EntryAmounts(salesAmount, commissionRate, commissionAmount, settlementAmount);
    }

    /**
     * 직접 금액을 지정하여 생성합니다 (역분개 등).
     *
     * @param salesAmount 원 판매 금액
     * @param commissionRate 수수료율
     * @param commissionAmount 수수료 금액
     * @param settlementAmount 정산 금액
     * @return EntryAmounts
     */
    public static EntryAmounts of(
            Money salesAmount, int commissionRate, Money commissionAmount, Money settlementAmount) {
        return new EntryAmounts(salesAmount, commissionRate, commissionAmount, settlementAmount);
    }
}
