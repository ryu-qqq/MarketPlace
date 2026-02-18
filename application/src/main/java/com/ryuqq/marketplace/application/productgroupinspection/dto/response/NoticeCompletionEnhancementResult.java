package com.ryuqq.marketplace.application.productgroupinspection.dto.response;

import java.util.List;

/**
 * 고시정보 LLM 보완 결과.
 *
 * @param fieldSuggestions 필드별 보완 제안 목록
 * @param enhancedCount 보완된 항목 수
 */
public record NoticeCompletionEnhancementResult(
        List<NoticeFieldSuggestion> fieldSuggestions, int enhancedCount) {

    /**
     * 개별 고시정보 필드 보완 제안.
     *
     * @param noticeFieldId 고시정보 필드 ID
     * @param fieldName 필드명
     * @param suggestedValue LLM이 제안한 값
     * @param confidence LLM 신뢰도 (0.0~1.0)
     */
    public record NoticeFieldSuggestion(
            Long noticeFieldId, String fieldName, String suggestedValue, double confidence) {}
}
