package com.ryuqq.marketplace.application.legacy.image.port.in.command;

import com.ryuqq.marketplace.application.legacy.image.dto.command.LegacyUpdateImagesCommand;

/** 레거시 상품 이미지 수정 UseCase. */
public interface LegacyProductUpdateImagesUseCase {

    void execute(LegacyUpdateImagesCommand command);
}
