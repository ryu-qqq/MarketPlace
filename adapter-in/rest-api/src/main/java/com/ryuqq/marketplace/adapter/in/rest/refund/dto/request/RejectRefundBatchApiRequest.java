package com.ryuqq.marketplace.adapter.in.rest.refund.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 환불 거절 일괄 API 요청. */
@Schema(description = "환불 거절 일괄 요청")
public record RejectRefundBatchApiRequest(
        @Schema(description = "환불 클레임 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED) @NotEmpty
                List<String> refundClaimIds) {}
