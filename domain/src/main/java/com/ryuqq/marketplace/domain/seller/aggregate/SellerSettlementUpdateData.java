package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;

/**
 * SellerSettlement 수정 데이터 Value Object.
 *
 * @param bankAccount 정산 계좌
 * @param settlementCycle 정산 주기
 * @param settlementDay 정산일
 */
public record SellerSettlementUpdateData(
        BankAccount bankAccount, SettlementCycle settlementCycle, Integer settlementDay) {

    public static SellerSettlementUpdateData of(
            BankAccount bankAccount, SettlementCycle settlementCycle, Integer settlementDay) {
        return new SellerSettlementUpdateData(bankAccount, settlementCycle, settlementDay);
    }
}
