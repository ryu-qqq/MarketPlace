package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 정산 원장 일괄 보류 처리 요청 DTO. */
public record SettlementHoldBatchApiRequest(
        @NotEmpty(message = "보류 처리할 정산 ID 목록은 필수입니다") List<String> settlementIds,
        @NotBlank(message = "보류 사유는 필수입니다") String holdReason) {}
