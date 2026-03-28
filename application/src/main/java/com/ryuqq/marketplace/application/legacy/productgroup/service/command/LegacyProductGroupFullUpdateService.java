package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullUpdateUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품그룹 전체 수정 서비스.
 *
 * <p>Factory에서 Command PK resolve, BundleFactory에서 도메인 번들 생성, 표준 Coordinator에서 diff 기반 수정.
 */
@Service
public class LegacyProductGroupFullUpdateService implements LegacyProductGroupFullUpdateUseCase {

    private final LegacyProductIdResolveFactory resolveFactory;
    private final ProductGroupBundleFactory bundleFactory;
    private final FullProductGroupUpdateCoordinator coordinator;

    public LegacyProductGroupFullUpdateService(
            LegacyProductIdResolveFactory resolveFactory,
            ProductGroupBundleFactory bundleFactory,
            FullProductGroupUpdateCoordinator coordinator) {
        this.resolveFactory = resolveFactory;
        this.bundleFactory = bundleFactory;
        this.coordinator = coordinator;
    }

    @Override
    public void execute(UpdateProductGroupFullCommand command) {
        UpdateProductGroupFullCommand resolvedCommand =
                resolveFactory.resolveUpdateFullCommand(command);
        ProductGroupUpdateBundle bundle = bundleFactory.createUpdateBundle(resolvedCommand);
        coordinator.update(bundle);
    }
}
