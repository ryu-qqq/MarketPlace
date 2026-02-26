package com.ryuqq.marketplace.domain.productintelligence.vo;

/** 분석 결과의 정보 출처. */
public enum AnalysisSource {

    /** Description HTML 텍스트에서 추출. */
    DESCRIPTION_TEXT("상세설명텍스트"),

    /** 이미지 멀티모달 분석으로 추출 (OCR 대체). */
    IMAGE_MULTIMODAL("이미지분석"),

    /** LLM 추론으로 도출. */
    LLM_INFERENCE("LLM추론"),

    /** Rule Engine 기반 자동 매핑. */
    RULE_ENGINE("룰엔진");

    private final String description;

    AnalysisSource(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
