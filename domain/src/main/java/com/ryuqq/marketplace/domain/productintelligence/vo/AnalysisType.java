package com.ryuqq.marketplace.domain.productintelligence.vo;

/** 분석 유형. 각 Analyzer Worker의 유형을 식별합니다. */
public enum AnalysisType {

    /** Description 분석 (텍스트 + 이미지 멀티모달). */
    DESCRIPTION("상세설명분석"),

    /** Option 분석 (캐노니컬 옵션 매핑). */
    OPTION("옵션매핑분석"),

    /** Notice 분석 (고시정보 보강). */
    NOTICE("고시정보분석");

    private final String description;

    AnalysisType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
