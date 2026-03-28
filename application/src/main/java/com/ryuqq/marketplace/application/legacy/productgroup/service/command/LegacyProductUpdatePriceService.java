package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdatePriceUseCase;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 가격 수정 서비스.
 *
 * <p>레거시에서는 상품그룹 단위 가격 변경이지만, market 스키마에서는 Product(SKU) 단위입니다. Factory에서 PK resolve, Coordinator에서
 * 일괄 변경합니다.
 */
@Service
public class LegacyProductUpdatePriceService implements LegacyProductUpdatePriceUseCase {

    private final LegacyProductIdResolveFactory resolveFactory;
    private final LegacyProductBulkCommandCoordinator bulkCommandCoordinator;

    public LegacyProductUpdatePriceService(
            LegacyProductIdResolveFactory resolveFactory,
            LegacyProductBulkCommandCoordinator bulkCommandCoordinator) {
        this.resolveFactory = resolveFactory;
        this.bulkCommandCoordinator = bulkCommandCoordinator;
    }

    @Override
    public void execute(long productGroupId, long regularPrice, long currentPrice) {
        ResolvedLegacyProductIds resolved = resolveFactory.resolve(productGroupId);
        bulkCommandCoordinator.updatePriceAll(
                resolved.resolvedProductGroupId(),
                Money.of((int) regularPrice),
                Money.of((int) currentPrice),
                resolveFactory.now());
    }
}
