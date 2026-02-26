package com.ryuqq.marketplace.application.commoncodetype.port.in.query;

import com.ryuqq.marketplace.application.commoncodetype.dto.query.CommonCodeTypeSearchParams;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;

/**
 * 공통 코드 타입 검색 UseCase.
 *
 * <p>APP-ASM-001: CommonCodeTypePageResult로 페이징 결과 반환
 */
public interface SearchCommonCodeTypeUseCase {

    CommonCodeTypePageResult execute(CommonCodeTypeSearchParams params);
}
