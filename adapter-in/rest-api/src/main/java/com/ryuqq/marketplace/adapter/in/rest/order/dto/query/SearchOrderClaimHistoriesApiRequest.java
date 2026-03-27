package com.ryuqq.marketplace.adapter.in.rest.order.dto.query;

import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import io.swagger.v3.oas.annotations.media.Schema;

/** 주문 클레임 이력 조회 요청. */
@Schema(description = "주문 클레임 이력 조회 요청")
public record SearchOrderClaimHistoriesApiRequest(
        @Schema(description = "클레임 타입 필터 (ORDER, CANCEL, REFUND, EXCHANGE). null이면 전체")
                ClaimType claimType,
        @Schema(description = "페이지 번호 (0부터)", example = "0") Integer page,
        @Schema(description = "페이지 크기", example = "20") Integer size) {}
