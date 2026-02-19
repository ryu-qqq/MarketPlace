package com.ryuqq.marketplace.domain.externalbrandmapping;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingStatus;

/** ExternalBrandMapping 도메인 테스트 Fixtures. */
public final class ExternalBrandMappingFixtures {

    private ExternalBrandMappingFixtures() {}

    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BR001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "외부 브랜드 A";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 100L;

    // ===== ID Fixtures =====
    public static ExternalBrandMappingId defaultId() {
        return ExternalBrandMappingId.of(1L);
    }

    public static ExternalBrandMappingId id(Long value) {
        return ExternalBrandMappingId.of(value);
    }

    // ===== Aggregate Fixtures =====
    public static ExternalBrandMapping newMapping() {
        return ExternalBrandMapping.forNew(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                CommonVoFixtures.now());
    }

    public static ExternalBrandMapping newMapping(
            Long externalSourceId, String externalBrandCode, Long internalBrandId) {
        return ExternalBrandMapping.forNew(
                externalSourceId,
                externalBrandCode,
                externalBrandCode + " Brand",
                internalBrandId,
                CommonVoFixtures.now());
    }

    public static ExternalBrandMapping activeMapping() {
        return ExternalBrandMapping.reconstitute(
                ExternalBrandMappingId.of(1L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                ExternalBrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalBrandMapping activeMapping(Long id) {
        return ExternalBrandMapping.reconstitute(
                ExternalBrandMappingId.of(id),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                ExternalBrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalBrandMapping activeMapping(
            Long id, Long externalSourceId, String externalBrandCode, Long internalBrandId) {
        return ExternalBrandMapping.reconstitute(
                ExternalBrandMappingId.of(id),
                externalSourceId,
                externalBrandCode,
                externalBrandCode + " Brand",
                internalBrandId,
                ExternalBrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalBrandMapping inactiveMapping() {
        return ExternalBrandMapping.reconstitute(
                ExternalBrandMappingId.of(2L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                "BR_INACTIVE",
                "비활성 브랜드",
                200L,
                ExternalBrandMappingStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
