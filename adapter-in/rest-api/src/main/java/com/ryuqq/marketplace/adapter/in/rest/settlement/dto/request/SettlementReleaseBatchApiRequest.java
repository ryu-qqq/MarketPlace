package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 정산 원장 일괄 보류 해제 요청 DTO. */
public record SettlementReleaseBatchApiRequest(
        @NotEmpty(message = "보류 해제할 정산 ID 목록은 필수입니다") List<String> settlementIds) {}
