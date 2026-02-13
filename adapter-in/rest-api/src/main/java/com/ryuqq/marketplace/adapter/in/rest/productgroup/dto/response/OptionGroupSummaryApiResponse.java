package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 옵션 그룹 요약 API 응답 DTO. */
@Schema(description = "옵션 그룹 요약 응답")
public record OptionGroupSummaryApiResponse(
        @Schema(description = "옵션 그룹명", example = "색상") String optionGroupName,
        @Schema(description = "옵션 값 이름 목록", example = "[\"블랙\", \"화이트\"]")
                List<String> optionValueNames) {}
