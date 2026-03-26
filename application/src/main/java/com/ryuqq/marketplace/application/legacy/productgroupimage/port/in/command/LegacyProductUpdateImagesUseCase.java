package com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;

/**
 * 레거시 상품 이미지 수정 UseCase.
 *
 * <p>표준 커맨드를 받되, luxurydb에 저장합니다.
 */
public interface LegacyProductUpdateImagesUseCase {

    void execute(UpdateProductGroupImagesCommand command);
}
