package com.ryuqq.marketplace.application.legacy.productgroup.manager;

import com.ryuqq.marketplace.application.legacy.productgroup.port.out.query.LegacyProductGroupCompositeListQueryPort;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 Composite 목록 조회 매니저.
 *
 * <p>Port를 호출하여 3-Phase Query로 상품그룹 목록과 카운트를 조회합니다.
 */
@Component
public class LegacyProductGroupCompositeListReadManager {

    private final LegacyProductGroupCompositeListQueryPort compositeListQueryPort;

    public LegacyProductGroupCompositeListReadManager(
            LegacyProductGroupCompositeListQueryPort compositeListQueryPort) {
        this.compositeListQueryPort = compositeListQueryPort;
    }

    /**
     * 검색 조건으로 상품그룹 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 상품그룹 상세 번들 목록
     */
    public List<LegacyProductGroupDetailBundle> search(LegacyProductGroupSearchCriteria criteria) {
        return compositeListQueryPort.searchProductGroups(criteria);
    }

    /**
     * 검색 조건에 맞는 전체 건수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    public long count(LegacyProductGroupSearchCriteria criteria) {
        return compositeListQueryPort.count(criteria);
    }
}
