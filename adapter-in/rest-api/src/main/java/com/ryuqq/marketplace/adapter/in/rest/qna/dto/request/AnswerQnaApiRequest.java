package com.ryuqq.marketplace.adapter.in.rest.qna.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** QnA 답변 등록 요청 DTO. */
@Schema(description = "QnA 답변 등록 요청")
public record AnswerQnaApiRequest(
        @Schema(description = "답변 내용", example = "해당 상품은 Free 사이즈입니다.") @NotBlank String content,
        @Schema(description = "답변자 이름", example = "판매자A") @NotBlank String authorName,
        @Schema(description = "부모 답변 ID (대댓글 시)", example = "1") Long parentReplyId) {}
