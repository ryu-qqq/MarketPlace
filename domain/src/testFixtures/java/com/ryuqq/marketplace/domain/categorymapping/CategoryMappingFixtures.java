package com.ryuqq.marketplace.domain.categorymapping;

import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSortKey;
import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;

/**
 * CategoryMapping 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 CategoryMapping 관련 객체들을 생성합니다.
 */
public final class CategoryMappingFixtures {

    private CategoryMappingFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_PRESET_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_CATEGORY_ID = 200L;
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 20L;

    // ===== CategoryMapping Aggregate Fixtures =====
    public static CategoryMapping newCategoryMapping() {
        return CategoryMapping.forNew(
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                CommonVoFixtures.now());
    }

    public static CategoryMapping newCategoryMapping(
            Long presetId, Long salesChannelCategoryId, Long internalCategoryId) {
        return CategoryMapping.forNew(
                presetId, salesChannelCategoryId, internalCategoryId, CommonVoFixtures.now());
    }

    public static CategoryMapping activeCategoryMapping() {
        return CategoryMapping.reconstitute(
                CategoryMappingId.of(1L),
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                CategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CategoryMapping activeCategoryMapping(Long id) {
        return CategoryMapping.reconstitute(
                CategoryMappingId.of(id),
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                CategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CategoryMapping inactiveCategoryMapping() {
        return CategoryMapping.reconstitute(
                CategoryMappingId.of(2L),
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                CategoryMappingStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== SearchCriteria Fixtures =====
    public static CategoryMappingSearchCriteria defaultSearchCriteria() {
        return CategoryMappingSearchCriteria.defaultCriteria();
    }

    // ===== QueryContext Fixtures =====
    public static QueryContext<CategoryMappingSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(CategoryMappingSortKey.defaultKey());
    }
}
