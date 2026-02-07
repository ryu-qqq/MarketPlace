package com.ryuqq.marketplace.application.commoncode.port.in.query;

import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;

/**
 * 공통 코드 검색 UseCase.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface SearchCommonCodeUseCase {

    /**
     * 공통 코드 페이지 조회.
     *
     * @param params 검색 파라미터
     * @return 페이지 결과
     */
    CommonCodePageResult execute(CommonCodeSearchParams params);
}
