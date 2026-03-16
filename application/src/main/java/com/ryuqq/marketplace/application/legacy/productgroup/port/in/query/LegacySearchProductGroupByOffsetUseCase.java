package com.ryuqq.marketplace.application.legacy.productgroup.port.in.query;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;

/**
 * 레거시 상품그룹 목록 조회 UseCase (Offset 기반 페이징).
 *
 * <p>세토프 어드민 ProductGroupFilter 호환 방식으로 상품그룹 목록을 페이징 조회합니다.
 */
public interface LegacySearchProductGroupByOffsetUseCase {

    /**
     * 레거시 상품그룹 목록을 조회합니다.
     *
     * @param params 검색 조건 파라미터
     * @return 페이징 처리된 상품그룹 목록 결과
     */
    LegacyProductGroupPageResult execute(LegacyProductGroupSearchParams params);
}
