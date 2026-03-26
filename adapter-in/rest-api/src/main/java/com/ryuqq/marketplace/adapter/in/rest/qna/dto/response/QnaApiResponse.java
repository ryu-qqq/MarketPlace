package com.ryuqq.marketplace.adapter.in.rest.qna.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** QnA 조회 응답 DTO. */
@Schema(description = "QnA 상세 응답")
public record QnaApiResponse(
        @Schema(description = "QnA ID", example = "1") long qnaId,
        @Schema(description = "셀러 ID", example = "1") long sellerId,
        @Schema(description = "상품그룹 ID", example = "100") long productGroupId,
        @Schema(description = "주문 ID (주문 문의일 때, nullable)", example = "200") Long orderId,
        @Schema(
                        description = "문의 유형",
                        allowableValues = {
                            "PRODUCT",
                            "SHIPPING",
                            "ORDER",
                            "EXCHANGE",
                            "REFUND",
                            "RESTOCK",
                            "PRICE",
                            "ETC"
                        },
                        example = "PRODUCT")
                String qnaType,
        @Schema(description = "판매채널 ID", example = "1") long salesChannelId,
        @Schema(description = "외부 QnA ID", example = "EXT-QNA-001") String externalQnaId,
        @Schema(description = "질문 제목") String questionTitle,
        @Schema(description = "질문 내용") String questionContent,
        @Schema(description = "질문자") String questionAuthor,
        @Schema(
                        description = "상태",
                        allowableValues = {"PENDING", "ANSWERED", "CLOSED"},
                        example = "PENDING")
                String status,
        @Schema(description = "답변 목록") List<QnaReplyApiResponse> replies,
        @Schema(description = "생성일시 (yyyy-MM-dd HH:mm:ss, KST)", example = "2026-03-26 15:00:00")
                String createdAt,
        @Schema(description = "수정일시 (yyyy-MM-dd HH:mm:ss, KST)", example = "2026-03-26 15:30:00")
                String updatedAt) {}
