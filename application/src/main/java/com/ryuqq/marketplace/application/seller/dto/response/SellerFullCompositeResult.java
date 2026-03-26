package com.ryuqq.marketplace.application.seller.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * SellerFullCompositeResult - 셀러 전체 Composite 조회 결과.
 *
 * <p>SellerCompositeResult(기본정보 + 주소 + 사업자정보 + CS)와 SellerPolicyCompositeResult(배송정책 + 환불정책),
 * ContractInfo(계약정보), SettlementInfo(정산정보)를 조합한 결과.
 */
public record SellerFullCompositeResult(
        SellerCompositeResult sellerComposite,
        SellerPolicyCompositeResult policyComposite,
        ContractInfo contractInfo,
        SettlementInfo settlementInfo) {

    public record ContractInfo(
            Long id,
            BigDecimal commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            String status,
            String specialTerms,
            Instant createdAt,
            Instant updatedAt) {}

    public record SettlementInfo(
            Long id,
            String bankCode,
            String bankName,
            String accountNumber,
            String accountHolderName,
            String settlementCycle,
            Integer settlementDay,
            boolean verified,
            Instant verifiedAt,
            Instant createdAt,
            Instant updatedAt) {}
}
