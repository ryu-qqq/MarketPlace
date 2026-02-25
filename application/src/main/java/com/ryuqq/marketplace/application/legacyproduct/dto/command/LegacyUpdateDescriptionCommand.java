package com.ryuqq.marketplace.application.legacyproduct.dto.command;

/**
 * 레거시 상품 상세설명 수정 Command.
 *
 * @param productGroupId 레거시 상품그룹 PK
 * @param detailDescription 상세설명 HTML
 */
public record LegacyUpdateDescriptionCommand(long productGroupId, String detailDescription) {}
