package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.id.SellerBusinessInfoId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import org.springframework.stereotype.Component;

/**
 * SellerBusinessInfoJpaEntityMapper - 셀러 사업자 정보 Entity-Domain 매퍼.
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
public class SellerBusinessInfoJpaEntityMapper {

    public SellerBusinessInfoJpaEntity toEntity(SellerBusinessInfo domain) {
        return SellerBusinessInfoJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.registrationNumberValue(),
                domain.companyNameValue(),
                domain.representativeValue(),
                domain.saleReportNumberValue(),
                domain.businessAddressZipCode(),
                domain.businessAddressRoad(),
                domain.businessAddressDetail(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    public SellerBusinessInfo toDomain(SellerBusinessInfoJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? SellerBusinessInfoId.of(entity.getId())
                        : SellerBusinessInfoId.forNew();
        return SellerBusinessInfo.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                RegistrationNumber.of(entity.getRegistrationNumber()),
                CompanyName.of(entity.getCompanyName()),
                Representative.of(entity.getRepresentative()),
                entity.getSaleReportNumber() != null
                        ? SaleReportNumber.of(entity.getSaleReportNumber())
                        : null,
                Address.of(
                        entity.getBusinessZipcode(),
                        entity.getBusinessAddress(),
                        entity.getBusinessAddressDetail()),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
