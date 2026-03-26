package com.ryuqq.marketplace.adapter.in.rest.qna.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** QnA 답변 조회 응답 DTO. */
@Schema(description = "QnA 답변 응답")
public record QnaReplyApiResponse(
        @Schema(description = "답변 ID", example = "1") long replyId,
        @Schema(description = "부모 답변 ID (대댓글, nullable)", example = "null") Long parentReplyId,
        @Schema(description = "답변 내용") String content,
        @Schema(description = "답변자") String authorName,
        @Schema(
                        description = "답변 유형",
                        allowableValues = {"SELLER_ANSWER", "BUYER_FOLLOW_UP"},
                        example = "SELLER_ANSWER")
                String replyType,
        @Schema(description = "생성일시 (yyyy-MM-dd HH:mm:ss, KST)", example = "2026-03-26 15:00:00")
                String createdAt) {}
