package com.ryuqq.marketplace.domain.brandpreset;

import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSortKey;
import com.ryuqq.marketplace.domain.brandpreset.vo.BrandPresetStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;

/**
 * BrandPreset 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 BrandPreset 관련 객체들을 생성합니다.
 */
public final class BrandPresetFixtures {

    private BrandPresetFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SHOP_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_BRAND_ID = 100L;
    public static final String DEFAULT_PRESET_NAME = "테스트 브랜드 프리셋";

    // ===== BrandPreset Aggregate Fixtures =====
    public static BrandPreset newBrandPreset() {
        return BrandPreset.forNew(
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                CommonVoFixtures.now());
    }

    public static BrandPreset newBrandPreset(Long shopId, Long salesChannelBrandId, String name) {
        return BrandPreset.forNew(shopId, salesChannelBrandId, name, CommonVoFixtures.now());
    }

    public static BrandPreset activeBrandPreset() {
        return BrandPreset.reconstitute(
                BrandPresetId.of(1L),
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                BrandPresetStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static BrandPreset activeBrandPreset(Long id) {
        return BrandPreset.reconstitute(
                BrandPresetId.of(id),
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                BrandPresetStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static BrandPreset activeBrandPreset(Long id, Long shopId) {
        return BrandPreset.reconstitute(
                BrandPresetId.of(id),
                shopId,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                BrandPresetStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static BrandPreset inactiveBrandPreset() {
        return BrandPreset.reconstitute(
                BrandPresetId.of(2L),
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                BrandPresetStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== SearchCriteria Fixtures =====
    public static BrandPresetSearchCriteria defaultSearchCriteria() {
        return new BrandPresetSearchCriteria(
                null,
                null,
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(BrandPresetSortKey.defaultKey()));
    }

    public static BrandPresetSearchCriteria searchCriteriaWithSalesChannel(
            List<Long> salesChannelIds) {
        return new BrandPresetSearchCriteria(
                salesChannelIds,
                null,
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(BrandPresetSortKey.defaultKey()));
    }

    public static BrandPresetSearchCriteria searchCriteriaWithPaging(int page, int size) {
        return new BrandPresetSearchCriteria(
                null,
                null,
                null,
                null,
                null,
                null,
                QueryContext.of(
                        BrandPresetSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(page, size)));
    }

    // ===== QueryContext Fixtures =====
    public static QueryContext<BrandPresetSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(BrandPresetSortKey.defaultKey());
    }
}
