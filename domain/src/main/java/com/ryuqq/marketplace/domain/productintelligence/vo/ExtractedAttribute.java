package com.ryuqq.marketplace.domain.productintelligence.vo;

import java.time.Instant;

/**
 * AI가 추출한 상품 속성 Value Object.
 *
 * <p>Description 텍스트/이미지에서 LLM이 추출한 구조화된 속성을 표현합니다. 예: { key: "material", value: "면 95%, 폴리 5%",
 * confidence: 0.95, source: OCR_IMAGE }
 */
public record ExtractedAttribute(
        String key,
        String value,
        ConfidenceScore confidence,
        AnalysisSource source,
        String sourceDetail,
        Instant extractedAt) {

    public ExtractedAttribute {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("ExtractedAttribute key는 필수입니다");
        }
        if (value == null) {
            throw new IllegalArgumentException("ExtractedAttribute value는 null일 수 없습니다");
        }
        if (confidence == null) {
            throw new IllegalArgumentException("ExtractedAttribute confidence는 필수입니다");
        }
        if (source == null) {
            throw new IllegalArgumentException("ExtractedAttribute source는 필수입니다");
        }
    }

    public static ExtractedAttribute of(
            String key, String value, double confidence, AnalysisSource source, Instant now) {
        return new ExtractedAttribute(
                key, value, ConfidenceScore.of(confidence), source, null, now);
    }

    public static ExtractedAttribute of(
            String key,
            String value,
            double confidence,
            AnalysisSource source,
            String sourceDetail,
            Instant now) {
        return new ExtractedAttribute(
                key, value, ConfidenceScore.of(confidence), source, sourceDetail, now);
    }

    public boolean isAutoApplicable() {
        return confidence.isAutoApplicable();
    }

    public double confidenceValue() {
        return confidence.value();
    }
}
