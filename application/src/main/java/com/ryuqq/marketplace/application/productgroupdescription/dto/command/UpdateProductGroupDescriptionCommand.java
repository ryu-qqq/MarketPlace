package com.ryuqq.marketplace.application.productgroupdescription.dto.command;

/**
 * UpdateProductGroupDescriptionCommand - 상품 그룹 상세 설명 수정 Command.
 *
 * <p>APP-CMD-001: Command Record 패턴
 *
 * <p>APP-CMD-002: Primitive 타입 사용 (DTO 레이어)
 */
public record UpdateProductGroupDescriptionCommand(long productGroupId, String content) {}
