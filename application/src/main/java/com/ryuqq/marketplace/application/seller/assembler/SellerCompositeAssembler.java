package com.ryuqq.marketplace.application.seller.assembler;

import com.ryuqq.marketplace.application.seller.dto.composite.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerFullCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerPolicyCompositeResult;
import org.springframework.stereotype.Component;

/**
 * SellerCompositeAssembler - 셀러 Composite 조합 Assembler.
 *
 * <p>SellerAdminCompositeResult와 SellerPolicyCompositeResult를 조합하여 SellerFullCompositeResult 생성.
 */
@Component
public class SellerCompositeAssembler {

    /**
     * Admin용 전체 조합.
     *
     * @param adminComposite Admin용 셀러 Composite (CS + Contract + Settlement 포함)
     * @param policyComposite 정책 Composite (ShippingPolicy + RefundPolicy)
     * @return 전체 조합 결과
     */
    public SellerFullCompositeResult assemble(
            SellerAdminCompositeResult adminComposite,
            SellerPolicyCompositeResult policyComposite) {
        SellerCompositeResult sellerComposite = toSellerCompositeResult(adminComposite);
        SellerFullCompositeResult.ContractInfo contractInfo =
                toContractInfo(adminComposite.contractInfo());
        SellerFullCompositeResult.SettlementInfo settlementInfo =
                toSettlementInfo(adminComposite.settlementInfo());

        return new SellerFullCompositeResult(
                sellerComposite, policyComposite, contractInfo, settlementInfo);
    }

    private SellerCompositeResult toSellerCompositeResult(
            SellerAdminCompositeResult adminComposite) {
        return new SellerCompositeResult(
                new SellerCompositeResult.SellerInfo(
                        adminComposite.seller().id(),
                        adminComposite.seller().sellerName(),
                        adminComposite.seller().displayName(),
                        adminComposite.seller().logoUrl(),
                        adminComposite.seller().description(),
                        adminComposite.seller().active(),
                        adminComposite.seller().createdAt(),
                        adminComposite.seller().updatedAt()),
                new SellerCompositeResult.BusinessInfo(
                        adminComposite.businessInfo().id(),
                        adminComposite.businessInfo().registrationNumber(),
                        adminComposite.businessInfo().companyName(),
                        adminComposite.businessInfo().representative(),
                        adminComposite.businessInfo().saleReportNumber(),
                        adminComposite.businessInfo().businessZipcode(),
                        adminComposite.businessInfo().businessAddress(),
                        adminComposite.businessInfo().businessAddressDetail()),
                new SellerCompositeResult.CsInfo(
                        adminComposite.csInfo().id(),
                        adminComposite.csInfo().csPhone(),
                        adminComposite.csInfo().csMobile(),
                        adminComposite.csInfo().csEmail(),
                        adminComposite.csInfo().operatingStartTime(),
                        adminComposite.csInfo().operatingEndTime(),
                        adminComposite.csInfo().operatingDays(),
                        adminComposite.csInfo().kakaoChannelUrl()));
    }

    private SellerFullCompositeResult.ContractInfo toContractInfo(
            SellerAdminCompositeResult.ContractInfo contractInfo) {

        return new SellerFullCompositeResult.ContractInfo(
                contractInfo.id(),
                contractInfo.commissionRate(),
                contractInfo.contractStartDate(),
                contractInfo.contractEndDate(),
                contractInfo.status(),
                contractInfo.specialTerms(),
                contractInfo.createdAt(),
                contractInfo.updatedAt());
    }

    private SellerFullCompositeResult.SettlementInfo toSettlementInfo(
            SellerAdminCompositeResult.SettlementInfo settlementInfo) {

        return new SellerFullCompositeResult.SettlementInfo(
                settlementInfo.id(),
                settlementInfo.bankCode(),
                settlementInfo.bankName(),
                settlementInfo.accountNumber(),
                settlementInfo.accountHolderName(),
                settlementInfo.settlementCycle(),
                settlementInfo.settlementDay(),
                settlementInfo.verified(),
                settlementInfo.verifiedAt(),
                settlementInfo.createdAt(),
                settlementInfo.updatedAt());
    }
}
