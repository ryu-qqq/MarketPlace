package com.ryuqq.marketplace.application.productgroupdescription.dto.command;

/**
 * RegisterProductGroupDescriptionCommand - 상품 그룹 상세 설명 등록 Command.
 *
 * <p>APP-CMD-001: Command Record 패턴
 *
 * <p>APP-CMD-002: Primitive 타입 사용 (DTO 레이어)
 */
public record RegisterProductGroupDescriptionCommand(long productGroupId, String content) {}
