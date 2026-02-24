package com.ryuqq.marketplace.application.legacyproduct.dto.command;

/**
 * 레거시 상품 품절 처리 Command.
 *
 * @param setofProductGroupId 세토프 상품그룹 PK
 */
public record LegacyMarkOutOfStockCommand(long setofProductGroupId) {}
