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
    public boolean existsBySalesChannelIdAndAccountId(Long salesChannelId, String accountId) {
        return repository.existsBySalesChannelIdAndAccountId(salesChannelId, accountId);
    }

    @Override
    public boolean existsBySalesChannelIdAndAccountIdExcluding(
            Long salesChannelId, String accountId, ShopId excludeId) {
        return repository.existsBySalesChannelIdAndAccountIdExcluding(
                salesChannelId, accountId, excludeId.value());
    }
}
