package com.ryuqq.marketplace.application.productgroup.port.in.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;

/**
 * 상품 그룹 등록 UseCase.
 *
 * <p>ProductGroup + Description + Notice + Products를 한번에 등록합니다.
 */
public interface RegisterProductGroupUseCase {

    /**
     * 상품 그룹을 등록합니다.
     *
     * @param command 등록 Command
     * @return 생성된 상품 그룹 ID
     */
    Long execute(RegisterProductGroupCommand command);
}
