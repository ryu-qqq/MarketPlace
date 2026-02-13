package com.ryuqq.marketplace.domain.settlement.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import java.util.List;

/** 정산 금액 정보 Value Object. */
public record SettlementAmounts(
        Money salesAmount,
        List<SettlementDeduction> deductions,
        Money feeAmount,
        int feeRate,
        Money expectedSettlementAmount,
        Money settlementAmount) {

    public SettlementAmounts {
        if (salesAmount == null) {
            throw new IllegalArgumentException("판매 금액은 null일 수 없습니다");
        }
        if (feeAmount == null) {
            throw new IllegalArgumentException("수수료 금액은 null일 수 없습니다");
        }
        if (feeRate < 0) {
            throw new IllegalArgumentException("수수료율은 0 이상이어야 합니다");
        }
        if (expectedSettlementAmount == null) {
            throw new IllegalArgumentException("예상 정산 금액은 null일 수 없습니다");
        }
        if (settlementAmount == null) {
            throw new IllegalArgumentException("정산 금액은 null일 수 없습니다");
        }
        deductions = deductions != null ? List.copyOf(deductions) : List.of();
    }

    /** 전체 차감 금액 합계. */
    public Money totalDeductionAmount() {
        Money total = Money.zero();
        for (SettlementDeduction deduction : deductions) {
            total = total.add(deduction.amount());
        }
        return total;
    }

    /** 셀러 부담 차감 금액 합계. */
    public Money sellerDeductionAmount() {
        Money total = Money.zero();
        for (SettlementDeduction deduction : deductions) {
            if (deduction.payer() == DeductionPayer.SELLER) {
                total = total.add(deduction.amount());
            }
        }
        return total;
    }

    /** 플랫폼 부담 차감 금액 합계. */
    public Money platformDeductionAmount() {
        Money total = Money.zero();
        for (SettlementDeduction deduction : deductions) {
            if (deduction.payer() == DeductionPayer.PLATFORM) {
                total = total.add(deduction.amount());
            }
        }
        return total;
    }
}
