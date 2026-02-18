package com.ryuqq.marketplace.application.productgroupimage.port.in.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;

/**
 * UpdateProductGroupImagesUseCase - 상품 그룹 이미지 수정 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface UpdateProductGroupImagesUseCase {

    /**
     * 상품 그룹 이미지를 수정합니다.
     *
     * @param command 수정할 이미지 Command
     */
    void execute(UpdateProductGroupImagesCommand command);
}
