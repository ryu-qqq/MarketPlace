package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.vo.CommissionRate;
import java.time.LocalDate;

/**
 * SellerContract 수정 데이터 Value Object.
 *
 * @param commissionRate 수수료율
 * @param contractStartDate 계약 시작일
 * @param contractEndDate 계약 종료일
 * @param specialTerms 특약사항
 */
public record SellerContractUpdateData(
        CommissionRate commissionRate,
        LocalDate contractStartDate,
        LocalDate contractEndDate,
        String specialTerms) {

    public static SellerContractUpdateData of(
            CommissionRate commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            String specialTerms) {
        return new SellerContractUpdateData(
                commissionRate, contractStartDate, contractEndDate, specialTerms);
    }
}
