package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.ContactInfo;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.DisplayName;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.vo.Agreement;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SellerApplicationJpaEntityMapper - 입점 신청 Entity-Domain 매퍼.
 *
 * <p>Entity ↔ Domain 변환을 담당합니다.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class SellerApplicationJpaEntityMapper {

    public SellerApplicationJpaEntity toEntity(SellerApplication domain) {
        Instant now = Instant.now();
        return SellerApplicationJpaEntity.create(
                domain.idValue(),
                domain.sellerNameValue(),
                domain.displayNameValue(),
                domain.logoUrlValue(),
                domain.descriptionValue(),
                domain.registrationNumberValue(),
                domain.companyNameValue(),
                domain.representativeValue(),
                domain.saleReportNumberValue(),
                domain.businessAddress().zipcode(),
                domain.businessAddress().line1(),
                domain.businessAddress().line2(),
                domain.csContact().phoneValue(),
                domain.csContact().emailValue(),
                domain.contactInfoName(),
                domain.contactInfoPhone(),
                domain.contactInfoEmail(),
                domain.bankCode(),
                domain.bankName(),
                domain.accountNumber(),
                domain.accountHolderName(),
                domain.settlementCycle().name(),
                domain.settlementDay(),
                domain.agreement().agreedAt(),
                domain.status(),
                domain.appliedAt(),
                domain.processedAt(),
                domain.processedBy(),
                domain.rejectionReason(),
                domain.approvedSellerIdValue(),
                domain.isNew() ? now : domain.appliedAt(),
                now);
    }

    public SellerApplication toDomain(SellerApplicationJpaEntity entity) {
        SellerApplicationId id =
                entity.getId() != null
                        ? SellerApplicationId.of(entity.getId())
                        : SellerApplicationId.forNew();
        return SellerApplication.reconstitute(
                id,
                SellerName.of(entity.getSellerName()),
                DisplayName.of(entity.getDisplayName()),
                entity.getLogoUrl() != null ? LogoUrl.of(entity.getLogoUrl()) : null,
                entity.getDescription() != null ? Description.of(entity.getDescription()) : null,
                RegistrationNumber.of(entity.getRegistrationNumber()),
                CompanyName.of(entity.getCompanyName()),
                Representative.of(entity.getRepresentative()),
                entity.getSaleReportNumber() != null
                        ? SaleReportNumber.of(entity.getSaleReportNumber())
                        : null,
                Address.of(
                        entity.getBusinessZipCode(),
                        entity.getBusinessBaseAddress(),
                        entity.getBusinessDetailAddress()),
                CsContact.of(entity.getCsPhoneNumber(), null, entity.getCsEmail()),
                entity.getContactName() != null
                        ? ContactInfo.of(
                                entity.getContactName(),
                                entity.getContactPhone(),
                                entity.getContactEmail())
                        : null,
                BankAccount.of(
                        entity.getBankCode(),
                        entity.getBankName(),
                        entity.getAccountNumber(),
                        entity.getAccountHolderName()),
                SettlementCycle.valueOf(entity.getSettlementCycle()),
                entity.getSettlementDay(),
                Agreement.reconstitute(entity.getAgreedAt()),
                entity.getStatus(),
                entity.getAppliedAt(),
                entity.getProcessedAt(),
                entity.getProcessedBy(),
                entity.getRejectionReason(),
                entity.getApprovedSellerId() != null
                        ? SellerId.of(entity.getApprovedSellerId())
                        : null);
    }
}
