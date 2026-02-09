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
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = ShopId.of(entity.getId());
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
