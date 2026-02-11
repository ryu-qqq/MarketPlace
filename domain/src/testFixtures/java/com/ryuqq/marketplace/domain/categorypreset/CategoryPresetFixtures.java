package com.ryuqq.marketplace.domain.categorypreset;

import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSortKey;
import com.ryuqq.marketplace.domain.categorypreset.vo.CategoryPresetStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;

/**
 * CategoryPreset 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 CategoryPreset 관련 객체들을 생성합니다.
 */
public final class CategoryPresetFixtures {

    private CategoryPresetFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SHOP_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_CATEGORY_ID = 200L;
    public static final String DEFAULT_PRESET_NAME = "테스트 카테고리 프리셋";

    // ===== CategoryPreset Aggregate Fixtures =====
    public static CategoryPreset newCategoryPreset() {
        return CategoryPreset.forNew(
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_PRESET_NAME,
                CommonVoFixtures.now());
    }

    public static CategoryPreset newCategoryPreset(
            Long shopId, Long salesChannelCategoryId, String name) {
        return CategoryPreset.forNew(shopId, salesChannelCategoryId, name, CommonVoFixtures.now());
    }

    public static CategoryPreset activeCategoryPreset() {
        return CategoryPreset.reconstitute(
                CategoryPresetId.of(1L),
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_PRESET_NAME,
                CategoryPresetStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CategoryPreset activeCategoryPreset(Long id) {
        return CategoryPreset.reconstitute(
                CategoryPresetId.of(id),
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_PRESET_NAME,
                CategoryPresetStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CategoryPreset activeCategoryPreset(Long id, Long shopId) {
        return CategoryPreset.reconstitute(
                CategoryPresetId.of(id),
                shopId,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_PRESET_NAME,
                CategoryPresetStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CategoryPreset inactiveCategoryPreset() {
        return CategoryPreset.reconstitute(
                CategoryPresetId.of(2L),
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                DEFAULT_PRESET_NAME,
                CategoryPresetStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== SearchCriteria Fixtures =====
    public static CategoryPresetSearchCriteria defaultSearchCriteria() {
        return new CategoryPresetSearchCriteria(
                null,
                null,
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(CategoryPresetSortKey.defaultKey()));
    }

    public static CategoryPresetSearchCriteria searchCriteriaWithSalesChannel(
            List<Long> salesChannelIds) {
        return new CategoryPresetSearchCriteria(
                salesChannelIds,
                null,
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(CategoryPresetSortKey.defaultKey()));
    }

    public static CategoryPresetSearchCriteria searchCriteriaWithPaging(int page, int size) {
        return new CategoryPresetSearchCriteria(
                null,
                null,
                null,
                null,
                null,
                null,
                QueryContext.of(
                        CategoryPresetSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(page, size)));
    }

    // ===== QueryContext Fixtures =====
    public static QueryContext<CategoryPresetSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(CategoryPresetSortKey.defaultKey());
    }
}
