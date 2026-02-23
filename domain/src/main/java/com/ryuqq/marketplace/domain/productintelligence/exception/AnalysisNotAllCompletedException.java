package com.ryuqq.marketplace.domain.productintelligence.exception;

/**
 * 모든 분석이 완료되지 않은 상태에서 집계를 시도하는 경우 예외.
 *
 * <p>AGGREGATING 전환 시 완료된 분석 수가 기대치에 미달할 때 발생합니다.
 */
public class AnalysisNotAllCompletedException extends ProductIntelligenceException {

    private static final ProductIntelligenceErrorCode ERROR_CODE =
            ProductIntelligenceErrorCode.ANALYSIS_NOT_ALL_COMPLETED;

    public AnalysisNotAllCompletedException(int completed, int expected) {
        super(ERROR_CODE, String.format("모든 분석이 완료되어야 합니다. 완료: %d/%d", completed, expected));
    }
}
