package com.ryuqq.marketplace.application.legacy.productgroup.port.out.query;

import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;

/**
 * 레거시 상품그룹 Composite 목록 조회 Port.
 *
 * <p>luxurydb에서 3-Phase Query로 상품그룹 목록 + 상품(SKU) 목록을 조회합니다.
 *
 * <p>Phase 1: 상품그룹 ID 목록 조회 (WHERE 조건 + offset/limit). Phase 2: 상품그룹 상세 조회 (ID IN, multi-table
 * JOIN). Phase 3: 상품+옵션 조회 (ID IN).
 */
public interface LegacyProductGroupCompositeListQueryPort {

    /**
     * 조건에 맞는 상품그룹 목록을 조회합니다 (3-Phase).
     *
     * @param criteria 검색 조건
     * @return 상품그룹 상세 번들 목록
     */
    List<LegacyProductGroupDetailBundle> searchProductGroups(
            LegacyProductGroupSearchCriteria criteria);

    /**
     * 조건에 맞는 상품그룹 전체 건수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    long count(LegacyProductGroupSearchCriteria criteria);
}
