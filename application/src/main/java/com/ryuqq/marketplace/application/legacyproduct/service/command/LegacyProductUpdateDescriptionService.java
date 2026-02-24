package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyDescriptionUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateDescriptionUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 상세설명 수정 서비스.
 *
 * <p>LegacyDescriptionUpdateCoordinator에 위임합니다.
 */
@Service
public class LegacyProductUpdateDescriptionService
        implements LegacyProductUpdateDescriptionUseCase {

    private final LegacyDescriptionUpdateCoordinator descriptionUpdateCoordinator;

    public LegacyProductUpdateDescriptionService(
            LegacyDescriptionUpdateCoordinator descriptionUpdateCoordinator) {
        this.descriptionUpdateCoordinator = descriptionUpdateCoordinator;
    }

    @Override
    public void execute(LegacyUpdateDescriptionCommand command) {
        descriptionUpdateCoordinator.execute(command);
    }
}
