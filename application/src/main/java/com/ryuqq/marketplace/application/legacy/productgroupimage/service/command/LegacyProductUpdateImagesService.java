package com.ryuqq.marketplace.application.legacy.productgroupimage.service.command;

import com.ryuqq.marketplace.application.legacy.productgroupimage.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacy.productgroupimage.internal.LegacyImageCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command.LegacyProductUpdateImagesUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 이미지 수정 서비스.
 *
 * <p>LegacyImageCommandCoordinator에 위임하여 diff 기반 이미지 업데이트를 수행합니다.
 */
@Service
public class LegacyProductUpdateImagesService implements LegacyProductUpdateImagesUseCase {

    private final LegacyImageCommandCoordinator imageCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateImagesService(
            LegacyImageCommandCoordinator imageCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateImagesCommand command) {
        imageCommandCoordinator.update(command);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
