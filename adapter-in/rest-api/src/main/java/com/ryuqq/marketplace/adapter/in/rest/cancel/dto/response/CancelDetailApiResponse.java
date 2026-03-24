package com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.CancelListItemApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimListItemApiResponseV4;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 취소 상세 응답.
 *
 * <p>V4 간극: orderId = 내부 orderItemId. legacyOrderId 제외.
 * 프론트는 목록/상세 모두 cancelInfo 래퍼 구조를 기대한다.
 */
@Schema(description = "취소 상세")
public record CancelDetailApiResponse(
        @Schema(description = "주문 ID (프론트: orderId = 내부 orderItemId)") String orderId,
        @Schema(description = "취소 정보") CancelListItemApiResponseV4.CancelInfoV4 cancelInfo,
        @Schema(description = "요청자") String requestedBy,
        @Schema(description = "처리자") String processedBy,
        @Schema(description = "처리일시") String processedAt,
        @Schema(description = "생성일시") String createdAt,
        @Schema(description = "수정일시") String updatedAt,
        @Schema(description = "클레임 이력 목록") List<ClaimHistoryApiResponse> claimHistories,
        @Schema(description = "결제 정보") ClaimListItemApiResponseV4.PaymentV4 payment) {}

