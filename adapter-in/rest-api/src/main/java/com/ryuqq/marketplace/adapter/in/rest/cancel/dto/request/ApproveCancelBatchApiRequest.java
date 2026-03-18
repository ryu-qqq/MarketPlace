package com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 취소 승인 일괄 요청. */
@Schema(description = "취소 승인 일괄 요청")
public record ApproveCancelBatchApiRequest(
        @Schema(description = "승인 대상 취소 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty
                List<String> cancelIds) {}
