package com.ryuqq.marketplace.application.legacy.qna.dto.result;

/**
 * 레거시 QnA 이미지 결과.
 *
 * @param qnaIssueType 이슈 유형
 * @param qnaImageId 이미지 ID
 * @param qnaId QnA ID (nullable)
 * @param qnaAnswerId 답변 ID (nullable)
 * @param imageUrl 이미지 URL
 * @param displayOrder 표시 순서
 */
public record LegacyQnaImageResult(
        String qnaIssueType,
        Long qnaImageId,
        Long qnaId,
        Long qnaAnswerId,
        String imageUrl,
        int displayOrder) {}
