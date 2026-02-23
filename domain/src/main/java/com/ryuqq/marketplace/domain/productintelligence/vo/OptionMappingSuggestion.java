package com.ryuqq.marketplace.domain.productintelligence.vo;

/**
 * 옵션 매핑 제안 Value Object.
 *
 * <p>셀러 옵션을 CanonicalOption에 매핑하는 AI/Rule 제안을 표현합니다.
 */
public record OptionMappingSuggestion(
        Long sellerOptionGroupId,
        Long sellerOptionValueId,
        String sellerOptionName,
        Long suggestedCanonicalGroupId,
        Long suggestedCanonicalValueId,
        String suggestedCanonicalValueName,
        ConfidenceScore confidence,
        AnalysisSource source,
        boolean appliedAutomatically) {

    public OptionMappingSuggestion {
        if (confidence == null) {
            throw new IllegalArgumentException("OptionMappingSuggestion confidence는 필수입니다");
        }
        if (source == null) {
            throw new IllegalArgumentException("OptionMappingSuggestion source는 필수입니다");
        }
    }

    public static OptionMappingSuggestion of(
            Long sellerOptionGroupId,
            Long sellerOptionValueId,
            String sellerOptionName,
            Long suggestedCanonicalGroupId,
            Long suggestedCanonicalValueId,
            String suggestedCanonicalValueName,
            double confidence,
            AnalysisSource source) {
        return new OptionMappingSuggestion(
                sellerOptionGroupId,
                sellerOptionValueId,
                sellerOptionName,
                suggestedCanonicalGroupId,
                suggestedCanonicalValueId,
                suggestedCanonicalValueName,
                ConfidenceScore.of(confidence),
                source,
                false);
    }

    /** 자동 적용 완료 상태로 복사. */
    public OptionMappingSuggestion markAsApplied() {
        return new OptionMappingSuggestion(
                sellerOptionGroupId,
                sellerOptionValueId,
                sellerOptionName,
                suggestedCanonicalGroupId,
                suggestedCanonicalValueId,
                suggestedCanonicalValueName,
                confidence,
                source,
                true);
    }

    public boolean isAutoApplicable() {
        return confidence.isAutoApplicable();
    }

    public double confidenceValue() {
        return confidence.value();
    }
}
