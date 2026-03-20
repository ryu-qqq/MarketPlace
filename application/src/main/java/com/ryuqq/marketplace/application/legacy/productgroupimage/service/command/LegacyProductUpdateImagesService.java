package com.ryuqq.marketplace.application.legacy.productgroupimage.service.command;

import com.ryuqq.marketplace.application.legacy.productgroupimage.manager.LegacyProductImageCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command.LegacyProductUpdateImagesUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 이미지 수정 서비스.
 *
 * <p>표준 커맨드를 Manager/Port에 위임하여 luxurydb에 저장합니다.
 */
@Service
public class LegacyProductUpdateImagesService implements LegacyProductUpdateImagesUseCase {

    private final LegacyProductImageCommandManager imageCommandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateImagesService(
            LegacyProductImageCommandManager imageCommandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.imageCommandManager = imageCommandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(UpdateProductGroupImagesCommand command) {
        imageCommandManager.update(command);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
