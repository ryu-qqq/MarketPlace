package com.ryuqq.marketplace.adapter.out.persistence.shop.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.shop.mapper.ShopJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopQueryDslRepository;
import com.ryuqq.marketplace.application.shop.port.out.query.ShopQueryPort;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Shop Query Adapter. */
@Component
public class ShopQueryAdapter implements ShopQueryPort {

    private final ShopQueryDslRepository repository;
    private final ShopJpaEntityMapper mapper;

    public ShopQueryAdapter(ShopQueryDslRepository repository, ShopJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Shop> findById(ShopId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Shop> findByCriteria(ShopSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(ShopSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public boolean existsByShopName(String shopName) {
        return repository.existsByShopName(shopName);
    }

    @Override
    public boolean existsByShopNameExcluding(String shopName, ShopId excludeId) {
        return repository.existsByShopNameExcluding(shopName, excludeId.value());
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return repository.existsByAccountId(accountId);
    }

    @Override
    public boolean existsByAccountIdExcluding(String accountId, ShopId excludeId) {
        return repository.existsByAccountIdExcluding(accountId, excludeId.value());
    }
}
