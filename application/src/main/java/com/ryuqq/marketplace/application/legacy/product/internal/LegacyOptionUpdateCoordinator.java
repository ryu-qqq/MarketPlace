package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductOptionCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductReadManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductStockCommandManager;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProducts;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductDiff;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOptionDiff;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 옵션/상품 수정 Coordinator.
 *
 * <p>Command를 받아 Factory로 도메인 객체를 생성하고, 기존 상품과 diff하여 추가/삭제/유지로 분류한 뒤 persist합니다. 유지 대상 옵션의
 * additionalPrice 변경도 처리합니다.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory.now()로 시각을 얻습니다.
 */
@Component
public class LegacyOptionUpdateCoordinator {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductReadManager productReadManager;
    private final LegacyProductCommandManager productCommandManager;
    private final LegacyProductOptionCommandManager optionCommandManager;
    private final LegacyProductStockCommandManager stockCommandManager;

    public LegacyOptionUpdateCoordinator(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductReadManager productReadManager,
            LegacyProductCommandManager productCommandManager,
            LegacyProductOptionCommandManager optionCommandManager,
            LegacyProductStockCommandManager stockCommandManager) {
        this.commandFactory = commandFactory;
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
        this.optionCommandManager = optionCommandManager;
        this.stockCommandManager = stockCommandManager;
    }

    /**
     * 옵션/상품 수정 Command 기반: Factory로 도메인 객체 생성 → 기존 상품 로드 → diff → persist.
     *
     * @param command 상품 옵션 수정 Command
     */
    @Transactional
    public void execute(LegacyUpdateProductsCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());
        List<LegacyProduct> newProducts =
                commandFactory.createProductsForOptionUpdate(groupId, command.skus());

        LegacyProducts existing = productReadManager.getByProductGroupId(groupId);
        LegacyProductDiff diff = existing.update(newProducts, commandFactory.now());

        if (!diff.hasNoChanges()) {
            persistProducts(diff);
            persistOptions(diff.optionDiff());
        }
    }

    private void persistProducts(LegacyProductDiff diff) {
        if (!diff.added().isEmpty()) {
            productCommandManager.persistAll(diff.added());
            stockCommandManager.persistAll(diff.added());
        }

        List<LegacyProduct> dirtyProducts = diff.allDirtyProducts();
        if (!dirtyProducts.isEmpty()) {
            productCommandManager.persistAll(dirtyProducts);
            stockCommandManager.persistAll(dirtyProducts);
        }
    }

    private void persistOptions(LegacyProductOptionDiff optionDiff) {
        if (!optionDiff.added().isEmpty()) {
            optionCommandManager.persistAll(optionDiff.added());
        }

        if (!optionDiff.allDirtyOptions().isEmpty()) {
            optionCommandManager.persistAll(optionDiff.allDirtyOptions());
        }
    }
}
