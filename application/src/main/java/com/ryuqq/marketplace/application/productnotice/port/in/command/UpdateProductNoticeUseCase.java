package com.ryuqq.marketplace.application.productnotice.port.in.command;

import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;

/**
 * UpdateProductNoticeUseCase - 상품 그룹 고시정보 수정 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface UpdateProductNoticeUseCase {

    /**
     * 상품 그룹 고시정보를 수정합니다.
     *
     * @param command 수정할 고시정보 Command
     */
    void execute(UpdateProductNoticeCommand command);
}
