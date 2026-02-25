package com.ryuqq.marketplace.application.legacyproduct.dto.command;

/**
 * 레거시 상품 전시 상태 변경 Command.
 *
 * @param productGroupId 레거시 상품그룹 PK
 * @param displayYn 전시 여부 ("Y" / "N")
 */
public record LegacyUpdateDisplayStatusCommand(long productGroupId, String displayYn) {}
