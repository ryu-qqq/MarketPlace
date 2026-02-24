package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;

/**
 * 레거시 상품 옵션/상품 수정 Command.
 *
 * <p>세토프 PK(setofProductGroupId)와 내부 UpdateProductsCommand를 함께 전달합니다. UseCase에서 setofProductGroupId
 * → internalId 변환 후 UpdateProductsUseCase에 위임합니다.
 *
 * @param setofProductGroupId 세토프 상품그룹 PK
 * @param command 옵션/상품 수정 Command (productGroupId는 placeholder, UseCase에서 internalId로 교체)
 */
public record LegacyUpdateProductsCommand(
        long setofProductGroupId, UpdateProductsCommand command) {}
