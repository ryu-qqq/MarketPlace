package com.ryuqq.marketplace.application.product.dto.command;

/**
 * 이름 기반 옵션 선택 DTO.
 *
 * <p>프론트엔드에서 옵션 그룹명 + 옵션 값명으로 직접 선택한 옵션을 표현합니다.
 *
 * @param optionGroupName 옵션 그룹명 (예: "색상")
 * @param optionValueName 옵션 값명 (예: "빨강")
 */
public record SelectedOption(String optionGroupName, String optionValueName) {}
