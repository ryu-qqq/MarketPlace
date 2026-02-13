package com.ryuqq.marketplace.application.productgroupdescription.port.in.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;

/**
 * UpdateProductGroupDescriptionUseCase - 상품 그룹 상세 설명 수정 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface UpdateProductGroupDescriptionUseCase {

    /**
     * 상품 그룹 상세 설명을 수정합니다.
     *
     * @param command 수정할 상세 설명 Command
     */
    void execute(UpdateProductGroupDescriptionCommand command);
}
