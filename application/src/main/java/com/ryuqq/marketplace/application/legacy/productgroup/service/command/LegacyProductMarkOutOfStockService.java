package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import org.springframework.stereotype.Service;

/**
 * 레거시 품절 처리 서비스.
 *
 * <p>해당 상품그룹의 모든 Product(SKU)를 SOLD_OUT + 재고 0으로 변경합니다.
 */
@Service
public class LegacyProductMarkOutOfStockService implements LegacyProductMarkOutOfStockUseCase {

    private final LegacyProductIdResolveFactory resolveFactory;
    private final LegacyProductBulkCommandCoordinator bulkCommandCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;

    public LegacyProductMarkOutOfStockService(
            LegacyProductIdResolveFactory resolveFactory,
            LegacyProductBulkCommandCoordinator bulkCommandCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase) {
        this.resolveFactory = resolveFactory;
        this.bulkCommandCoordinator = bulkCommandCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
    }

    @Override
    public LegacyProductGroupDetailResult execute(LegacyMarkOutOfStockCommand command) {
        ResolvedLegacyProductIds resolved = resolveFactory.resolve(command.productGroupId());
        bulkCommandCoordinator.markSoldOutAll(
                resolved.resolvedProductGroupId(), resolveFactory.now());
        return legacyProductQueryUseCase.execute(command.productGroupId());
    }
}
