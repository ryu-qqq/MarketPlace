package com.ryuqq.marketplace.adapter.in.rest.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 수기 메모 등록 응답. */
@Schema(description = "수기 메모 등록 응답")
public record ClaimHistoryMemoApiResponse(@Schema(description = "생성된 이력 ID") String historyId) {}
