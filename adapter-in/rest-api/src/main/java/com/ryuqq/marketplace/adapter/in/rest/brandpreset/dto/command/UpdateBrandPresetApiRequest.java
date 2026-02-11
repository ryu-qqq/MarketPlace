package com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 브랜드 프리셋 수정 API 요청 DTO. */
@Schema(description = "브랜드 프리셋 수정 요청")
public record UpdateBrandPresetApiRequest(
        @Schema(description = "프리셋 이름", example = "수정된 프리셋 이름") String presetName,
        @Schema(description = "판매채널 브랜드 ID", example = "10") Long salesChannelBrandId,
        @Schema(description = "매핑할 내부 브랜드 ID 목록", example = "[1, 2, 3]")
                List<Long> internalBrandIds) {}
