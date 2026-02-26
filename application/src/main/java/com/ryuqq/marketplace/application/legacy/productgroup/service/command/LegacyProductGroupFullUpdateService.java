package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.bundle.LegacyProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.factory.LegacyProductGroupBundleFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupFullUpdateCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullUpdateUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품그룹 전체 수정 서비스.
 *
 * <p>APP-SER-001: Service 레이어는 UseCase를 구현하고 Factory + Coordinator로 위임합니다.
 */
@Service
public class LegacyProductGroupFullUpdateService implements LegacyProductGroupFullUpdateUseCase {

    private final LegacyProductGroupBundleFactory bundleFactory;
    private final LegacyProductGroupFullUpdateCoordinator coordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductGroupFullUpdateService(
            LegacyProductGroupBundleFactory bundleFactory,
            LegacyProductGroupFullUpdateCoordinator coordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.bundleFactory = bundleFactory;
        this.coordinator = coordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateProductGroupCommand command) {
        LegacyProductGroupUpdateBundle bundle = bundleFactory.createUpdateBundle(command);
        coordinator.execute(bundle);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
