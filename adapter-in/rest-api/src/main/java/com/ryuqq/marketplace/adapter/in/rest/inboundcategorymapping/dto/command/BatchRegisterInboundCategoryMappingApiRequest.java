package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 외부 카테고리 매핑 일괄 등록 요청 DTO. */
@Schema(description = "외부 카테고리 매핑 일괄 등록 요청")
public record BatchRegisterInboundCategoryMappingApiRequest(
        @Schema(description = "매핑 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @Valid
                @NotEmpty(message = "매핑 목록은 비어있을 수 없습니다")
                List<MappingEntryRequest> entries) {

    @Schema(description = "매핑 항목")
    public record MappingEntryRequest(
            @Schema(
                            description = "외부 카테고리 코드",
                            example = "NV_CAT_001",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "외부 카테고리 코드는 필수입니다")
                    String externalCategoryCode,
            @Schema(
                            description = "외부 카테고리명",
                            example = "남성의류",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "외부 카테고리명은 필수입니다")
                    String externalCategoryName,
            @Schema(
                            description = "내부 카테고리 ID",
                            example = "1",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotNull(message = "내부 카테고리 ID는 필수입니다")
                    Long internalCategoryId) {}
}
