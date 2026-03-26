package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 정산 원장 일괄 완료 처리 요청 DTO. */
public record SettlementCompleteBatchApiRequest(
        @NotEmpty(message = "완료 처리할 정산 ID 목록은 필수입니다") List<String> settlementIds) {}
