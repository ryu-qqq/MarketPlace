package com.ryuqq.marketplace.application.shop.port.out.query;

import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import java.util.List;
import java.util.Optional;

/** Shop Query Port. */
public interface ShopQueryPort {
    Optional<Shop> findById(ShopId id);

    List<Shop> findByIds(List<ShopId> ids);

    List<Shop> findByCriteria(ShopSearchCriteria criteria);

    long countByCriteria(ShopSearchCriteria criteria);

    boolean existsBySalesChannelIdAndAccountId(Long salesChannelId, String accountId);

    boolean existsBySalesChannelIdAndAccountIdExcluding(
            Long salesChannelId, String accountId, ShopId excludeId);

    List<Shop> findActiveBySalesChannelId(Long salesChannelId);
}
