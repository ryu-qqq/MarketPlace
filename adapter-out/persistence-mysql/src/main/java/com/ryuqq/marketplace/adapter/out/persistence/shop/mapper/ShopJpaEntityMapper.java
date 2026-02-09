package com.ryuqq.marketplace.adapter.out.persistence.shop.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import org.springframework.stereotype.Component;

/** Shop JPA Entity Mapper. */
@Component
public class ShopJpaEntityMapper {

    public ShopJpaEntity toEntity(Shop shop) {
        return ShopJpaEntity.create(
                shop.idValue(),
                shop.shopName(),
                shop.accountId(),
                shop.status().name(),
                shop.createdAt(),
                shop.updatedAt(),
                shop.deletedAt());
    }

    public Shop toDomain(ShopJpaEntity entity) {
        var id = entity.getId() != null ? ShopId.of(entity.getId()) : ShopId.forNew();
        return Shop.reconstitute(
                id,
                entity.getShopName(),
                entity.getAccountId(),
                ShopStatus.fromString(entity.getStatus()),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
