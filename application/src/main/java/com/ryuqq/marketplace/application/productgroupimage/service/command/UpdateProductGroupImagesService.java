package com.ryuqq.marketplace.application.productgroupimage.service.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.port.in.command.UpdateProductGroupImagesUseCase;
import org.springframework.stereotype.Service;

/**
 * UpdateProductGroupImagesService - 상품 그룹 이미지 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class UpdateProductGroupImagesService implements UpdateProductGroupImagesUseCase {

    private final ImageCommandCoordinator imageCommandCoordinator;

    public UpdateProductGroupImagesService(ImageCommandCoordinator imageCommandCoordinator) {
        this.imageCommandCoordinator = imageCommandCoordinator;
    }

    @Override
    public void execute(UpdateProductGroupImagesCommand command) {
        imageCommandCoordinator.update(command);
    }
}
