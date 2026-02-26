package com.ryuqq.marketplace.domain.productintelligence.exception;

import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;

/**
 * 프로파일 상태 전환이 유효하지 않은 경우 예외.
 *
 * <p>현재 상태에서 요청된 액션을 수행할 수 없을 때 발생합니다.
 */
public class InvalidProfileStateException extends ProductIntelligenceException {

    private static final ProductIntelligenceErrorCode ERROR_CODE =
            ProductIntelligenceErrorCode.INVALID_PROFILE_STATE;

    public InvalidProfileStateException(AnalysisStatus current, String action) {
        super(ERROR_CODE, String.format("%s은 현재 상태(%s)에서 불가능합니다", action, current));
    }
}
