package com.ryuqq.marketplace.domain.inboundbrandmapping;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;

/** InboundBrandMapping 도메인 테스트 Fixtures. */
public final class InboundBrandMappingFixtures {

    private InboundBrandMappingFixtures() {}

    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BR001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "외부 브랜드 A";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 100L;

    // ===== ID Fixtures =====
    public static InboundBrandMappingId defaultId() {
        return InboundBrandMappingId.of(1L);
    }

    public static InboundBrandMappingId id(Long value) {
        return InboundBrandMappingId.of(value);
    }

    // ===== Aggregate Fixtures =====
    public static InboundBrandMapping newMapping() {
        return InboundBrandMapping.forNew(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                CommonVoFixtures.now());
    }

    public static InboundBrandMapping newMapping(
            Long inboundSourceId, String externalBrandCode, Long internalBrandId) {
        return InboundBrandMapping.forNew(
                inboundSourceId,
                externalBrandCode,
                externalBrandCode + " Brand",
                internalBrandId,
                CommonVoFixtures.now());
    }

    public static InboundBrandMapping activeMapping() {
        return InboundBrandMapping.reconstitute(
                InboundBrandMappingId.of(1L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                InboundBrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundBrandMapping activeMapping(Long id) {
        return InboundBrandMapping.reconstitute(
                InboundBrandMappingId.of(id),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                InboundBrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundBrandMapping activeMapping(
            Long id, Long inboundSourceId, String externalBrandCode, Long internalBrandId) {
        return InboundBrandMapping.reconstitute(
                InboundBrandMappingId.of(id),
                inboundSourceId,
                externalBrandCode,
                externalBrandCode + " Brand",
                internalBrandId,
                InboundBrandMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundBrandMapping inactiveMapping() {
        return InboundBrandMapping.reconstitute(
                InboundBrandMappingId.of(2L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                "BR_INACTIVE",
                "비활성 브랜드",
                200L,
                InboundBrandMappingStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
