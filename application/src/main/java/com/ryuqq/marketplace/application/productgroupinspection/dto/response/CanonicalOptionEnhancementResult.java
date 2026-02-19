package com.ryuqq.marketplace.application.productgroupinspection.dto.response;

import java.util.List;

/**
 * 캐노니컬 옵션 LLM 보강 결과.
 *
 * @param mappingSuggestions 매핑 제안 목록
 * @param enhancedCount 보강된 항목 수
 */
public record CanonicalOptionEnhancementResult(
        List<OptionMappingSuggestion> mappingSuggestions, int enhancedCount) {

    /**
     * 개별 옵션 매핑 제안.
     *
     * @param sellerOptionGroupId 셀러 옵션 그룹 ID
     * @param canonicalOptionGroupId 매핑 대상 캐노니컬 옵션 그룹 ID
     * @param sellerOptionValueId 셀러 옵션 값 ID
     * @param canonicalOptionValueId 매핑 대상 캐노니컬 옵션 값 ID
     * @param confidence LLM 신뢰도 (0.0~1.0)
     */
    public record OptionMappingSuggestion(
            Long sellerOptionGroupId,
            Long canonicalOptionGroupId,
            Long sellerOptionValueId,
            Long canonicalOptionValueId,
            double confidence) {}
}
