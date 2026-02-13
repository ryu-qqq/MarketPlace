package com.ryuqq.marketplace.application.productgroup.dto.composite;

import java.util.List;

/**
 * 옵션 그룹 요약 결과 DTO.
 *
 * <p>목록 조회에서 옵션 요약 정보를 제공합니다. (예: optionGroupName="색상", optionValueNames=["블랙", "화이트"])
 *
 * @param optionGroupName 옵션 그룹명 (예: "색상", "사이즈")
 * @param optionValueNames 옵션 값 이름 목록 (예: ["블랙", "화이트", "레드"])
 */
public record OptionGroupSummaryResult(String optionGroupName, List<String> optionValueNames) {

    public OptionGroupSummaryResult {
        optionValueNames = optionValueNames != null ? List.copyOf(optionValueNames) : List.of();
    }
}
