package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;

/**
 * 레거시 상품 이미지 수정 Command.
 *
 * @param setofProductGroupId 세토프 상품그룹 PK
 * @param command 이미지 수정 Command (productGroupId는 placeholder, UseCase에서 internalId로 교체)
 */
public record LegacyUpdateImagesCommand(
        long setofProductGroupId, UpdateProductGroupImagesCommand command) {}
