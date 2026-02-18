package com.ryuqq.marketplace.application.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import java.util.List;

/**
 * RegisterProductsUseCase - 상품(SKU) 일괄 등록 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface RegisterProductsUseCase {

    /**
     * 상품을 일괄 등록합니다.
     *
     * @param command 등록할 상품 Command
     * @return 생성된 상품 ID 목록
     */
    List<Long> execute(RegisterProductsCommand command);
}
