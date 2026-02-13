package com.ryuqq.marketplace.application.productgroup.port.in.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;

/**
 * UpdateProductGroupBasicInfoUseCase - 상품 그룹 기본 정보 수정 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface UpdateProductGroupBasicInfoUseCase {

    /**
     * 상품 그룹 기본 정보를 수정합니다.
     *
     * @param command 수정할 기본 정보 Command
     */
    void execute(UpdateProductGroupBasicInfoCommand command);
}
