package com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 정산 보류 요청. */
public record HoldSettlementApiRequest(@NotBlank(message = "보류 사유는 필수입니다") String reason) {}
