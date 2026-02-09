package com.ryuqq.marketplace.adapter.out.persistence.shop.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.mapper.ShopJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.application.shop.port.out.command.ShopCommandPort;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.springframework.stereotype.Component;

/** Shop Command Adapter. */
@Component
public class ShopCommandAdapter implements ShopCommandPort {

    private final ShopJpaRepository repository;
    private final ShopJpaEntityMapper mapper;

    public ShopCommandAdapter(ShopJpaRepository repository, ShopJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(Shop shop) {
        ShopJpaEntity entity = mapper.toEntity(shop);
        ShopJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
