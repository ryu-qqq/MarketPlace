package com.ryuqq.marketplace.application.shop.manager;

import com.ryuqq.marketplace.application.shop.port.out.query.ShopQueryPort;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.exception.ShopNotFoundException;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Shop Read Manager. */
@Component
public class ShopReadManager {

    private final ShopQueryPort queryPort;

    public ShopReadManager(ShopQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Shop getById(ShopId id) {
        return queryPort.findById(id).orElseThrow(() -> new ShopNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<Shop> findByCriteria(ShopSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ShopSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelIdAndAccountId(Long salesChannelId, String accountId) {
        return queryPort.existsBySalesChannelIdAndAccountId(salesChannelId, accountId);
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelIdAndAccountIdExcluding(
            Long salesChannelId, String accountId, ShopId excludeId) {
        return queryPort.existsBySalesChannelIdAndAccountIdExcluding(
                salesChannelId, accountId, excludeId);
    }
}
