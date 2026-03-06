package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** 상품 외부몰 전송 결과 응답 (API 5). */
@Schema(description = "상품 외부몰 전송 결과")
public record SyncProductsApiResponse(
        @Schema(description = "신규 생성 Outbox 수", example = "3") int createCount,
        @Schema(description = "수정 Outbox 수", example = "2") int updateCount,
        @Schema(description = "스킵된 수 (미연결/중복)", example = "1") int skippedCount,
        @Schema(description = "결과 상태", example = "ACCEPTED") String status) {}
