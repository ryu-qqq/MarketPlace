package com.ryuqq.marketplace.application.productnotice.port.in.command;

import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;

/**
 * RegisterProductNoticeUseCase - 상품 그룹 고시정보 등록 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface RegisterProductNoticeUseCase {

    /**
     * 상품 그룹 고시정보를 등록합니다.
     *
     * @param command 등록할 고시정보 Command
     * @return 생성된 고시정보 ID
     */
    Long execute(RegisterProductNoticeCommand command);
}
