package com.ryuqq.marketplace.application.productgroup.port.in.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;

/**
 * 상품 그룹 전체 수정 UseCase.
 *
 * <p>ProductGroup + Description + Notice + Products를 한번에 수정합니다.
 *
 * <p>Products는 기존 Product를 soft delete하고 새로 생성하는 전략을 사용합니다.
 */
public interface UpdateProductGroupFullUseCase {

    /**
     * 상품 그룹 전체를 수정합니다.
     *
     * @param command 수정 Command
     */
    void execute(UpdateProductGroupFullCommand command);
}
