package com.ryuqq.marketplace.application.product.port.in.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;

/**
 * UpdateProductsUseCase - 상품(SKU) + 옵션 수정 Use Case.
 *
 * <p>APP-UC-001: Single method interface
 */
public interface UpdateProductsUseCase {

    /**
     * 상품 그룹 하위 상품들의 옵션 구조와 가격/재고/SKU/정렬을 수정합니다.
     *
     * <p>옵션 그룹 diff → Product diff (retained/added/removed) 순서로 처리합니다.
     *
     * @param command 수정할 상품 Command (optionGroups + products)
     */
    void execute(UpdateProductsCommand command);
}
