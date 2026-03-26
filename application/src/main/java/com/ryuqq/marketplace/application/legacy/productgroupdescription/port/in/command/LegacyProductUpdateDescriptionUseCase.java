package com.ryuqq.marketplace.application.legacy.productgroupdescription.port.in.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;

/**
 * 레거시 상품 상세설명 수정 UseCase.
 *
 * <p>표준 커맨드를 받되, luxurydb에 저장합니다.
 */
public interface LegacyProductUpdateDescriptionUseCase {

    void execute(UpdateProductGroupDescriptionCommand command);
}
