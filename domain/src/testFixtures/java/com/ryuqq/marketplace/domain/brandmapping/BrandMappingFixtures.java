package com.ryuqq.marketplace.domain.brandmapping;

import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSortKey;
import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;

/**
 * BrandMapping 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 BrandMapping 관련 객체들을 생성합니다.
 */
public final class BrandMappingFixtures {

    private BrandMappingFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_PRESET_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_BRAND_ID = 100L;
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 10L;

    // ===== BrandMapping Aggregate Fixtures =====
    public static BrandMapping newBrandMapping() {
        return BrandMapping.forNew(
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_INTERNAL_BRAND_ID,
                CommonVoFixtures.now());
    }

    public static BrandMapping newBrandMapping(
            Long presetId, Long salesChannelBrandId, Long internalBrandId) {
        return BrandMapping.forNew(
                presetId, salesChannelBrandId, internalBrandId, CommonVoFixtures.now());
    }

    public static BrandMapping activeBrandMapping() {
        return BrandMapping.reconstitute(
                BrandMappingId.of(1L),
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_INTERNAL_BRAND_ID,
                BrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static BrandMapping activeBrandMapping(Long id) {
        return BrandMapping.reconstitute(
                BrandMappingId.of(id),
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_INTERNAL_BRAND_ID,
                BrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static BrandMapping inactiveBrandMapping() {
        return BrandMapping.reconstitute(
                BrandMappingId.of(2L),
                DEFAULT_PRESET_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_INTERNAL_BRAND_ID,
                BrandMappingStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== SearchCriteria Fixtures =====
    public static BrandMappingSearchCriteria defaultSearchCriteria() {
        return BrandMappingSearchCriteria.defaultCriteria();
    }

    // ===== QueryContext Fixtures =====
    public static QueryContext<BrandMappingSortKey> defaultQueryContext() {
        return QueryContext.defaultOf(BrandMappingSortKey.defaultKey());
    }
}
