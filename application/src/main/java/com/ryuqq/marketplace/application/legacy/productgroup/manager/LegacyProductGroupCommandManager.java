package com.ryuqq.marketplace.application.legacy.productgroup.manager;

import com.ryuqq.marketplace.application.legacy.productgroup.port.out.command.LegacyProductGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품그룹 Command Manager. */
@Component
public class LegacyProductGroupCommandManager {

    private final LegacyProductGroupCommandPort commandPort;

    public LegacyProductGroupCommandManager(LegacyProductGroupCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ProductGroup productGroup, long regularPrice, long currentPrice) {
        return commandPort.persist(productGroup, regularPrice, currentPrice);
    }

    @Transactional
    public void persist(ProductGroupUpdateData updateData, long regularPrice, long currentPrice) {
        commandPort.persist(updateData, regularPrice, currentPrice);
    }

    @Transactional
    public void updateDisplayYn(long productGroupId, String displayYn) {
        commandPort.updateDisplayYn(productGroupId, displayYn);
    }

    @Transactional
    public void markSoldOut(long productGroupId) {
        commandPort.markSoldOut(productGroupId);
    }

    @Transactional
    public void updatePrice(long productGroupId, long regularPrice, long currentPrice) {
        commandPort.updatePrice(productGroupId, regularPrice, currentPrice);
    }
}
