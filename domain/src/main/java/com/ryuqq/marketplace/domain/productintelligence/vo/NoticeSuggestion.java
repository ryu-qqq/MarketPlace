package com.ryuqq.marketplace.domain.productintelligence.vo;

/**
 * 고시정보 보강 제안 Value Object.
 *
 * <p>누락된 고시정보 필드에 대한 AI 보강 제안을 표현합니다.
 */
public record NoticeSuggestion(
        Long noticeFieldId,
        String fieldName,
        String currentValue,
        String suggestedValue,
        ConfidenceScore confidence,
        AnalysisSource source,
        boolean appliedAutomatically) {

    public NoticeSuggestion {
        if (noticeFieldId == null) {
            throw new IllegalArgumentException("NoticeSuggestion noticeFieldId는 필수입니다");
        }
        if (confidence == null) {
            throw new IllegalArgumentException("NoticeSuggestion confidence는 필수입니다");
        }
        if (source == null) {
            throw new IllegalArgumentException("NoticeSuggestion source는 필수입니다");
        }
    }

    public static NoticeSuggestion of(
            Long noticeFieldId,
            String fieldName,
            String currentValue,
            String suggestedValue,
            double confidence,
            AnalysisSource source) {
        return new NoticeSuggestion(
                noticeFieldId,
                fieldName,
                currentValue,
                suggestedValue,
                ConfidenceScore.of(confidence),
                source,
                false);
    }

    /** 자동 적용 완료 상태로 복사. */
    public NoticeSuggestion markAsApplied() {
        return new NoticeSuggestion(
                noticeFieldId, fieldName, currentValue, suggestedValue, confidence, source, true);
    }

    /** 현재 값이 비어있거나 무의미한지 확인. */
    public boolean isCurrentValueEmpty() {
        return currentValue == null || currentValue.isBlank();
    }

    public boolean isAutoApplicable() {
        return confidence.isAutoApplicable();
    }

    public double confidenceValue() {
        return confidence.value();
    }
}
