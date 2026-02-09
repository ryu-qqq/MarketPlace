package com.ryuqq.marketplace.application.shop.port.out.query;

import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import java.util.List;
import java.util.Optional;

/** Shop Query Port. */
public interface ShopQueryPort {
    Optional<Shop> findById(ShopId id);

    List<Shop> findByCriteria(ShopSearchCriteria criteria);

    long countByCriteria(ShopSearchCriteria criteria);

    boolean existsByShopName(String shopName);

    boolean existsByShopNameExcluding(String shopName, ShopId excludeId);

    boolean existsByAccountId(String accountId);

    boolean existsByAccountIdExcluding(String accountId, ShopId excludeId);
}
