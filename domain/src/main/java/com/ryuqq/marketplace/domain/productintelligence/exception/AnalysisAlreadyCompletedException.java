package com.ryuqq.marketplace.domain.productintelligence.exception;

import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;

/**
 * 이미 완료된 분석을 다시 수행하려는 경우 예외.
 *
 * <p>동일한 분석 타입에 대해 중복 결과 기록을 시도할 때 발생합니다.
 */
public class AnalysisAlreadyCompletedException extends ProductIntelligenceException {

    private static final ProductIntelligenceErrorCode ERROR_CODE =
            ProductIntelligenceErrorCode.ANALYSIS_ALREADY_COMPLETED;

    public AnalysisAlreadyCompletedException(AnalysisType type) {
        super(ERROR_CODE, String.format("%s 분석은 이미 완료되었습니다", type));
    }
}
