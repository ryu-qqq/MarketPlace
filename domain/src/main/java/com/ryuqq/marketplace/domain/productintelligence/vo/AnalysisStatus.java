package com.ryuqq.marketplace.domain.productintelligence.vo;

/**
 * 상품 프로파일 분석 상태.
 *
 * <p>파이프라인 흐름: PENDING → ORCHESTRATING → ANALYZING → AGGREGATING → COMPLETED / FAILED
 */
@SuppressWarnings("PMD.DataClass")
public enum AnalysisStatus {

    /** 대기 중. Orchestrator 처리 전. */
    PENDING("대기"),

    /** 오케스트레이션 중. 데이터 수집 및 Analyzer 분배 진행 중. */
    ORCHESTRATING("오케스트레이션중"),

    /** 분석 중. 각 Analyzer Worker가 병렬로 처리 중. */
    ANALYZING("분석중"),

    /** 집계 중. 모든 분석 완료 후 Aggregator가 판정 중. */
    AGGREGATING("집계중"),

    /** 완료. 최종 판정 완료. */
    COMPLETED("완료"),

    /** 실패. 처리 중 오류 발생. */
    FAILED("실패");

    private final String description;

    AnalysisStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isAnalyzing() {
        return this == ANALYZING;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    /** 종료 상태(COMPLETED/FAILED) 여부. */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }

    /** 진행 중 상태 여부. 타임아웃 복구 대상. */
    public boolean isInProgress() {
        return this == ORCHESTRATING || this == ANALYZING || this == AGGREGATING;
    }
}
