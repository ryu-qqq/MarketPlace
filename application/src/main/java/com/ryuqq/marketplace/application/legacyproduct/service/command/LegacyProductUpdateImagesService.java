package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyImageUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateImagesUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 이미지 수정 서비스.
 *
 * <p>LegacyImageUpdateCoordinator에 위임합니다.
 */
@Service
public class LegacyProductUpdateImagesService implements LegacyProductUpdateImagesUseCase {

    private final LegacyImageUpdateCoordinator imageUpdateCoordinator;

    public LegacyProductUpdateImagesService(LegacyImageUpdateCoordinator imageUpdateCoordinator) {
        this.imageUpdateCoordinator = imageUpdateCoordinator;
    }

    @Override
    public void execute(LegacyUpdateImagesCommand command) {
        imageUpdateCoordinator.execute(command);
    }
}
