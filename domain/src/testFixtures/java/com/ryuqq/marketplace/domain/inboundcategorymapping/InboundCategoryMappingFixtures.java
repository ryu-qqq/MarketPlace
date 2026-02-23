package com.ryuqq.marketplace.domain.inboundcategorymapping;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;

/** InboundCategoryMapping 도메인 테스트 Fixtures. */
public final class InboundCategoryMappingFixtures {

    private InboundCategoryMappingFixtures() {}

    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT_SHOES_001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "외부 카테고리 신발";
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 100L;

    // ===== ID Fixtures =====
    public static InboundCategoryMappingId defaultId() {
        return InboundCategoryMappingId.of(1L);
    }

    public static InboundCategoryMappingId id(Long value) {
        return InboundCategoryMappingId.of(value);
    }

    // ===== Aggregate Fixtures =====
    public static InboundCategoryMapping newMapping() {
        return InboundCategoryMapping.forNew(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                CommonVoFixtures.now());
    }

    public static InboundCategoryMapping newMapping(
            Long inboundSourceId, String externalCategoryCode, Long internalCategoryId) {
        return InboundCategoryMapping.forNew(
                inboundSourceId,
                externalCategoryCode,
                externalCategoryCode + " Category",
                internalCategoryId,
                CommonVoFixtures.now());
    }

    public static InboundCategoryMapping activeMapping() {
        return InboundCategoryMapping.reconstitute(
                InboundCategoryMappingId.of(1L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                InboundCategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundCategoryMapping activeMapping(Long id) {
        return InboundCategoryMapping.reconstitute(
                InboundCategoryMappingId.of(id),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                InboundCategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundCategoryMapping activeMapping(
            Long id, Long inboundSourceId, String externalCategoryCode, Long internalCategoryId) {
        return InboundCategoryMapping.reconstitute(
                InboundCategoryMappingId.of(id),
                inboundSourceId,
                externalCategoryCode,
                externalCategoryCode + " Category",
                internalCategoryId,
                InboundCategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundCategoryMapping inactiveMapping() {
        return InboundCategoryMapping.reconstitute(
                InboundCategoryMappingId.of(2L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                "CAT_INACTIVE",
                "비활성 카테고리",
                200L,
                InboundCategoryMappingStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
