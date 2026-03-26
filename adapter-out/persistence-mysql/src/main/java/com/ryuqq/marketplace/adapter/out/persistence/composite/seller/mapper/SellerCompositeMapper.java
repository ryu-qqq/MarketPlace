package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerAdminCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerCompositeDto;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerCompositeResult;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

/**
 * SellerCompositeMapper - Composite DTO 변환 Mapper.
 *
 * <p>Persistence DTO를 Application Result로 변환.
 *
 * <p>LocalTime → String 변환 등 타입 변환 처리.
 */
@Component
public class SellerCompositeMapper {

    public SellerCompositeResult toResult(SellerCompositeDto dto) {
        return new SellerCompositeResult(
                new SellerCompositeResult.SellerInfo(
                        dto.sellerId(),
                        dto.sellerName(),
                        dto.displayName(),
                        dto.logoUrl(),
                        dto.description(),
                        dto.active(),
                        dto.sellerCreatedAt(),
                        dto.sellerUpdatedAt()),
                new SellerCompositeResult.BusinessInfo(
                        dto.businessInfoId(),
                        dto.registrationNumber(),
                        dto.companyName(),
                        dto.representative(),
                        dto.saleReportNumber(),
                        dto.businessZipcode(),
                        dto.businessAddress(),
                        dto.businessAddressDetail()),
                new SellerCompositeResult.CsInfo(
                        dto.csId(),
                        dto.csPhone(),
                        dto.csMobile(),
                        dto.csEmail(),
                        toNullableTimeString(dto.operatingStartTime()),
                        toNullableTimeString(dto.operatingEndTime()),
                        dto.operatingDays(),
                        dto.kakaoChannelUrl()));
    }

    public SellerAdminCompositeResult toAdminResult(SellerAdminCompositeDto dto) {
        return new SellerAdminCompositeResult(
                new SellerAdminCompositeResult.SellerInfo(
                        dto.sellerId(),
                        dto.sellerName(),
                        dto.displayName(),
                        dto.logoUrl(),
                        dto.description(),
                        dto.active(),
                        dto.sellerCreatedAt(),
                        dto.sellerUpdatedAt()),
                new SellerAdminCompositeResult.BusinessInfo(
                        dto.businessInfoId(),
                        dto.registrationNumber(),
                        dto.companyName(),
                        dto.representative(),
                        dto.saleReportNumber(),
                        dto.businessZipcode(),
                        dto.businessAddress(),
                        dto.businessAddressDetail()),
                new SellerAdminCompositeResult.CsInfo(
                        dto.csId(),
                        dto.csPhone(),
                        dto.csMobile(),
                        dto.csEmail(),
                        toNullableTimeString(dto.operatingStartTime()),
                        toNullableTimeString(dto.operatingEndTime()),
                        dto.operatingDays(),
                        dto.kakaoChannelUrl()),
                new SellerAdminCompositeResult.ContractInfo(
                        dto.contractId(),
                        dto.commissionRate(),
                        dto.contractStartDate(),
                        dto.contractEndDate(),
                        dto.contractStatus(),
                        dto.specialTerms(),
                        dto.contractCreatedAt(),
                        dto.contractUpdatedAt()),
                new SellerAdminCompositeResult.SettlementInfo(
                        dto.settlementId(),
                        dto.bankCode(),
                        dto.bankName(),
                        dto.accountNumber(),
                        dto.accountHolderName(),
                        dto.settlementCycle(),
                        dto.settlementDay(),
                        dto.verified(),
                        dto.verifiedAt(),
                        dto.settlementCreatedAt(),
                        dto.settlementUpdatedAt()));
    }

    private String toNullableTimeString(LocalTime time) {
        return time != null ? time.toString() : null;
    }
}
