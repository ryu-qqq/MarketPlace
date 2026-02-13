package com.ryuqq.marketplace.domain.settlement.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;

/** 정산 차감 항목 Value Object. */
public record SettlementDeduction(
        DeductionType type, DeductionPayer payer, Money amount, String description) {

    public SettlementDeduction {
        if (type == null) {
            throw new IllegalArgumentException("차감 유형은 null일 수 없습니다");
        }
        if (payer == null) {
            throw new IllegalArgumentException("차감 부담 주체는 null일 수 없습니다");
        }
        if (amount == null) {
            throw new IllegalArgumentException("차감 금액은 null일 수 없습니다");
        }
    }

    public static SettlementDeduction of(
            DeductionType type, DeductionPayer payer, Money amount, String description) {
        return new SettlementDeduction(type, payer, amount, description);
    }
}
