package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressName;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import org.springframework.stereotype.Component;

/**
 * SellerAddressJpaEntityMapper - 셀러 주소 Entity-Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 */
@Component
public class SellerAddressJpaEntityMapper {

    public SellerAddressJpaEntity toEntity(SellerAddress domain) {
        return SellerAddressJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.addressType().name(),
                domain.addressNameValue(),
                domain.addressZipCode(),
                domain.addressRoad(),
                domain.addressDetail(),
                domain.isDefaultAddress(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    public SellerAddress toDomain(SellerAddressJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? SellerAddressId.of(entity.getId())
                        : SellerAddressId.forNew();
        return SellerAddress.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                AddressType.valueOf(entity.getAddressType()),
                AddressName.of(entity.getAddressName()),
                Address.of(entity.getZipcode(), entity.getAddress(), entity.getAddressDetail()),
                entity.isDefaultAddress(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
