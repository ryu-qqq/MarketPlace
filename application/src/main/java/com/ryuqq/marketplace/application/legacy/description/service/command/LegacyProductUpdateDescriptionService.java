package com.ryuqq.marketplace.application.legacy.description.service.command;

import com.ryuqq.marketplace.application.legacy.description.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacy.description.internal.LegacyDescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.description.port.in.command.LegacyProductUpdateDescriptionUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 상세설명 수정 서비스.
 *
 * <p>APP-SER-001: Service 레이어는 UseCase를 구현하고 Coordinator로 위임합니다.
 */
@Service
public class LegacyProductUpdateDescriptionService
        implements LegacyProductUpdateDescriptionUseCase {

    private final LegacyDescriptionCommandCoordinator descriptionCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateDescriptionService(
            LegacyDescriptionCommandCoordinator descriptionCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateDescriptionCommand command) {
        descriptionCommandCoordinator.update(command);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
