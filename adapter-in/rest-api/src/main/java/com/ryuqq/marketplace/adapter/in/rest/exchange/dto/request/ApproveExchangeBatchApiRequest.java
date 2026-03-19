package com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 교환 승인 일괄 API 요청 (수거 시작). */
@Schema(description = "교환 승인 일괄 요청")
public record ApproveExchangeBatchApiRequest(
        @Schema(description = "교환 클레임 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED) @NotEmpty
                List<String> exchangeClaimIds) {}
