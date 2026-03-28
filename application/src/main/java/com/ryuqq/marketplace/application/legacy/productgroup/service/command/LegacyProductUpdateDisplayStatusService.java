package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 전시 상태 변경 서비스.
 *
 * <p>displayYn=Y → 모든 Product ACTIVE, displayYn=N → 모든 Product INACTIVE.
 */
@Service
public class LegacyProductUpdateDisplayStatusService
        implements LegacyProductUpdateDisplayStatusUseCase {

    private final LegacyProductIdResolveFactory resolveFactory;
    private final LegacyProductBulkCommandCoordinator bulkCommandCoordinator;

    public LegacyProductUpdateDisplayStatusService(
            LegacyProductIdResolveFactory resolveFactory,
            LegacyProductBulkCommandCoordinator bulkCommandCoordinator) {
        this.resolveFactory = resolveFactory;
        this.bulkCommandCoordinator = bulkCommandCoordinator;
    }

    @Override
    public void execute(LegacyUpdateDisplayStatusCommand command) {
        ResolvedLegacyProductIds resolved = resolveFactory.resolve(command.productGroupId());
        ProductStatus targetStatus =
                "Y".equals(command.displayYn()) ? ProductStatus.ACTIVE : ProductStatus.INACTIVE;
        bulkCommandCoordinator.changeStatusAll(
                resolved.resolvedProductGroupId(), targetStatus, resolveFactory.now());
    }
}
