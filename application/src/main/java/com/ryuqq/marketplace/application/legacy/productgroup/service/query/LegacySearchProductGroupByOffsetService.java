package com.ryuqq.marketplace.application.legacy.productgroup.service.query;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.productgroup.factory.LegacyProductGroupQueryFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupListReadFacade;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacySearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.application.legacy.shared.assembler.LegacyProductGroupAssembler;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품그룹 목록 조회 서비스 (Offset 기반 페이징).
 *
 * <p>카테고리 ID 확장 → QueryFactory → ReadFacade → Assembler 흐름으로 상품그룹 목록을 조회합니다.
 */
@Service
public class LegacySearchProductGroupByOffsetService
        implements LegacySearchProductGroupByOffsetUseCase {

    private final LegacyProductGroupListReadFacade readFacade;
    private final LegacyProductGroupQueryFactory queryFactory;
    private final LegacyProductGroupAssembler assembler;
    private final CategoryReadManager categoryReadManager;

    public LegacySearchProductGroupByOffsetService(
            LegacyProductGroupListReadFacade readFacade,
            LegacyProductGroupQueryFactory queryFactory,
            LegacyProductGroupAssembler assembler,
            CategoryReadManager categoryReadManager) {
        this.readFacade = readFacade;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
        this.categoryReadManager = categoryReadManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyProductGroupPageResult execute(LegacyProductGroupSearchParams params) {
        LegacyProductGroupSearchParams expandedParams = expandCategoryIds(params);
        LegacyProductGroupSearchCriteria criteria = queryFactory.createCriteria(expandedParams);
        List<LegacyProductGroupDetailBundle> bundles = readFacade.getBundles(criteria);
        long totalElements = readFacade.count(criteria);
        return assembler.toPageResult(bundles, totalElements, criteria.page(), criteria.size());
    }

    private LegacyProductGroupSearchParams expandCategoryIds(
            LegacyProductGroupSearchParams params) {
        if (params.categoryIds() == null || params.categoryIds().isEmpty()) {
            return params;
        }
        List<Long> expandedIds =
                categoryReadManager.expandWithDescendants(params.categoryIds());
        return params.withCategoryIds(expandedIds);
    }
}
