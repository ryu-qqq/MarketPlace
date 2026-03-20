package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.dto.LegacySellerCompositeQueryDto;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * 레거시 셀러 Composite Mapper.
 *
 * <p>레거시 QueryDto(seller + seller_business_info)를 표준 {@link SellerAdminCompositeResult}로 변환합니다.
 */
@Component
public class LegacySellerCompositeMapper {

    public SellerAdminCompositeResult toResult(LegacySellerCompositeQueryDto dto) {
        return new SellerAdminCompositeResult(
                toSellerInfo(dto),
                toBusinessInfo(dto),
                toCsInfo(dto),
                toContractInfo(dto),
                toSettlementInfo(dto));
    }

    private SellerAdminCompositeResult.SellerInfo toSellerInfo(LegacySellerCompositeQueryDto dto) {
        return new SellerAdminCompositeResult.SellerInfo(
                dto.sellerId(),
                dto.sellerName(),
                dto.sellerName(),
                defaultString(dto.sellerLogoUrl()),
                defaultString(dto.sellerDescription()),
                true,
                null,
                null);
    }

    private SellerAdminCompositeResult.BusinessInfo toBusinessInfo(
            LegacySellerCompositeQueryDto dto) {
        return new SellerAdminCompositeResult.BusinessInfo(
                dto.sellerId(),
                defaultString(dto.registrationNumber()),
                defaultString(dto.companyName()),
                defaultString(dto.representative()),
                defaultString(dto.saleReportNumber()),
                defaultString(dto.businessAddressZipCode()),
                defaultString(dto.businessAddressLine1()),
                defaultString(dto.businessAddressLine2()));
    }

    private SellerAdminCompositeResult.CsInfo toCsInfo(LegacySellerCompositeQueryDto dto) {
        return new SellerAdminCompositeResult.CsInfo(
                dto.sellerId(),
                defaultString(dto.csNumber()),
                defaultString(dto.csPhoneNumber()),
                defaultString(dto.csEmail()),
                "",
                "",
                "",
                "");
    }

    private SellerAdminCompositeResult.ContractInfo toContractInfo(
            LegacySellerCompositeQueryDto dto) {
        return new SellerAdminCompositeResult.ContractInfo(
                dto.sellerId(),
                dto.commissionRate() != null
                        ? BigDecimal.valueOf(dto.commissionRate())
                        : BigDecimal.ZERO,
                null,
                null,
                "",
                "",
                null,
                null);
    }

    private SellerAdminCompositeResult.SettlementInfo toSettlementInfo(
            LegacySellerCompositeQueryDto dto) {
        return new SellerAdminCompositeResult.SettlementInfo(
                dto.sellerId(),
                "",
                defaultString(dto.bankName()),
                defaultString(dto.accountNumber()),
                defaultString(dto.accountHolderName()),
                "",
                null,
                false,
                null,
                null,
                null);
    }

    private String defaultString(String value) {
        return value != null ? value : "";
    }
}
