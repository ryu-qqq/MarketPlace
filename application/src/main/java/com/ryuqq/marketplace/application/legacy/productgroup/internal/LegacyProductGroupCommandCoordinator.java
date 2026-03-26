package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 기본정보 Coordinator.
 *
 * <p>등록과 수정 모두 이 Coordinator를 통합니다.
 */
@Component
public class LegacyProductGroupCommandCoordinator {

    private final LegacyProductGroupCommandManager commandManager;

    public LegacyProductGroupCommandCoordinator(LegacyProductGroupCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /** 상품그룹 등록 후 productGroupId 반환. */
    public Long register(ProductGroup productGroup, long regularPrice, long currentPrice) {
        return commandManager.persist(productGroup, regularPrice, currentPrice);
    }

    /** 상품그룹 수정. 조회 없이 바로 UPDATE. */
    public void update(ProductGroupUpdateData updateData, long regularPrice, long currentPrice) {
        commandManager.persist(updateData, regularPrice, currentPrice);
    }

    /** 전시 상태 변경. */
    public void updateDisplayYn(long productGroupId, String displayYn) {
        commandManager.updateDisplayYn(productGroupId, displayYn);
    }

    /** 품절 처리. */
    public void markSoldOut(long productGroupId) {
        commandManager.markSoldOut(productGroupId);
    }

    /** 가격 수정. */
    public void updatePrice(long productGroupId, long regularPrice, long currentPrice) {
        commandManager.updatePrice(productGroupId, regularPrice, currentPrice);
    }
}
