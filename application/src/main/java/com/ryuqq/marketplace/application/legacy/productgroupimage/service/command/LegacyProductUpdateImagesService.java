package com.ryuqq.marketplace.application.legacy.productgroupimage.service.command;

import com.ryuqq.marketplace.application.legacy.productgroupimage.internal.LegacyImageCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command.LegacyProductUpdateImagesUseCase;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 이미지 수정 서비스.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class LegacyProductUpdateImagesService implements LegacyProductUpdateImagesUseCase {

    private final LegacyImageCommandCoordinator imageCommandCoordinator;

    public LegacyProductUpdateImagesService(LegacyImageCommandCoordinator imageCommandCoordinator) {
        this.imageCommandCoordinator = imageCommandCoordinator;
    }

    @Override
    public void execute(UpdateProductGroupImagesCommand command) {
        imageCommandCoordinator.update(command);
    }
}
