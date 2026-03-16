package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupCompositeListReadManager;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 목록 Read Facade.
 *
 * <p>CompositeListReadManager를 호출하여 상품그룹 목록과 카운트를 함께 조회합니다.
 * Service에서 직접 Manager를 여러 번 호출하지 않도록 오케스트레이션을 담당합니다.
 */
@Component
public class LegacyProductGroupListReadFacade {

    private final LegacyProductGroupCompositeListReadManager compositeListReadManager;

    public LegacyProductGroupListReadFacade(
            LegacyProductGroupCompositeListReadManager compositeListReadManager) {
        this.compositeListReadManager = compositeListReadManager;
    }

    /**
     * 상품그룹 번들 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 상품그룹 상세 번들 목록
     */
    public List<LegacyProductGroupDetailBundle> getBundles(
            LegacyProductGroupSearchCriteria criteria) {
        return compositeListReadManager.search(criteria);
    }

    /**
     * 전체 건수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    public long count(LegacyProductGroupSearchCriteria criteria) {
        return compositeListReadManager.count(criteria);
    }
}
