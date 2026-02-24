package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;

/**
 * 레거시 상품 상세설명 수정 Command.
 *
 * @param setofProductGroupId 세토프 상품그룹 PK
 * @param command 상세설명 수정 Command (productGroupId는 placeholder, UseCase에서 internalId로 교체)
 */
public record LegacyUpdateDescriptionCommand(
        long setofProductGroupId, UpdateProductGroupDescriptionCommand command) {}
