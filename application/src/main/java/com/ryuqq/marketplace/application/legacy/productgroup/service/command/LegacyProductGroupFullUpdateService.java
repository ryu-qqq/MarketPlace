package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupFullUpdateCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullUpdateUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품그룹 전체 수정 서비스.
 *
 * <p>표준 UpdateProductGroupFullCommand → BundleFactory로 Bundle 생성 → Coordinator로 luxurydb UPDATE.
 */
@Service
public class LegacyProductGroupFullUpdateService implements LegacyProductGroupFullUpdateUseCase {

    private final ProductGroupBundleFactory bundleFactory;
    private final LegacyProductGroupFullUpdateCoordinator coordinator;

    public LegacyProductGroupFullUpdateService(
            ProductGroupBundleFactory bundleFactory,
            LegacyProductGroupFullUpdateCoordinator coordinator) {
        this.bundleFactory = bundleFactory;
        this.coordinator = coordinator;
    }

    @Override
    public void execute(UpdateProductGroupFullCommand command) {
        ProductGroupUpdateBundle bundle = bundleFactory.createUpdateBundle(command);
        coordinator.update(bundle);
    }
}
