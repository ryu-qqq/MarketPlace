package com.ryuqq.marketplace.adapter.in.rest.qna.dto.request;

import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/** QnA 목록 조회 요청 DTO. */
@Schema(description = "QnA 목록 조회 요청")
public record SearchQnaApiRequest(
        @Schema(description = "셀러 ID", example = "1") @Min(1) long sellerId,
        @Schema(description = "QnA 상태 필터", example = "PENDING") QnaStatus status,
        @Schema(description = "페이지 번호 (0-based)", example = "0") @Min(0) Integer page,
        @Schema(description = "페이지 크기", example = "20") @Min(1) Integer size) {
    public int resolvedPage() {
        return page != null ? page : 0;
    }

    public int resolvedSize() {
        return size != null ? size : 20;
    }
}
