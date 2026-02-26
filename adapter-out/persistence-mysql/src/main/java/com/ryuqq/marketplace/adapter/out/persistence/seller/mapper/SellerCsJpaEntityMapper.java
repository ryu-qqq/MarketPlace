package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;
import org.springframework.stereotype.Component;

/**
 * SellerCsJpaEntityMapper - 셀러 CS 정보 Entity-Domain 매퍼.
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
public class SellerCsJpaEntityMapper {

    public SellerCsJpaEntity toEntity(SellerCs domain) {
        return SellerCsJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.csPhone(),
                domain.csMobile(),
                domain.csEmail(),
                domain.operatingHours() != null ? domain.operatingHours().startTime() : null,
                domain.operatingHours() != null ? domain.operatingHours().endTime() : null,
                domain.operatingDays(),
                domain.kakaoChannelUrl(),
                domain.createdAt(),
                domain.updatedAt(),
                null);
    }

    public SellerCs toDomain(SellerCsJpaEntity entity) {
        CsContact csContact =
                CsContact.of(entity.getCsPhone(), entity.getCsMobile(), entity.getCsEmail());

        OperatingHours operatingHours = null;
        if (entity.getOperatingStartTime() != null && entity.getOperatingEndTime() != null) {
            operatingHours =
                    OperatingHours.of(entity.getOperatingStartTime(), entity.getOperatingEndTime());
        }

        var id = entity.getId() != null ? SellerCsId.of(entity.getId()) : SellerCsId.forNew();
        return SellerCs.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                csContact,
                operatingHours,
                entity.getOperatingDays(),
                entity.getKakaoChannelUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
