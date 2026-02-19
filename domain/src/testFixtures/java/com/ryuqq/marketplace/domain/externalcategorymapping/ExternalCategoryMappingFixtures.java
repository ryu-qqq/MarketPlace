package com.ryuqq.marketplace.domain.externalcategorymapping;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;

/** ExternalCategoryMapping 도메인 테스트 Fixtures. */
public final class ExternalCategoryMappingFixtures {

    private ExternalCategoryMappingFixtures() {}

    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT_SHOES_001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "외부 카테고리 신발";
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 100L;

    // ===== ID Fixtures =====
    public static ExternalCategoryMappingId defaultId() {
        return ExternalCategoryMappingId.of(1L);
    }

    public static ExternalCategoryMappingId id(Long value) {
        return ExternalCategoryMappingId.of(value);
    }

    // ===== Aggregate Fixtures =====
    public static ExternalCategoryMapping newMapping() {
        return ExternalCategoryMapping.forNew(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                CommonVoFixtures.now());
    }

    public static ExternalCategoryMapping newMapping(
            Long externalSourceId, String externalCategoryCode, Long internalCategoryId) {
        return ExternalCategoryMapping.forNew(
                externalSourceId,
                externalCategoryCode,
                externalCategoryCode + " Category",
                internalCategoryId,
                CommonVoFixtures.now());
    }

    public static ExternalCategoryMapping activeMapping() {
        return ExternalCategoryMapping.reconstitute(
                ExternalCategoryMappingId.of(1L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                ExternalCategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalCategoryMapping activeMapping(Long id) {
        return ExternalCategoryMapping.reconstitute(
                ExternalCategoryMappingId.of(id),
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                ExternalCategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalCategoryMapping activeMapping(
            Long id, Long externalSourceId, String externalCategoryCode, Long internalCategoryId) {
        return ExternalCategoryMapping.reconstitute(
                ExternalCategoryMappingId.of(id),
                externalSourceId,
                externalCategoryCode,
                externalCategoryCode + " Category",
                internalCategoryId,
                ExternalCategoryMappingStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalCategoryMapping inactiveMapping() {
        return ExternalCategoryMapping.reconstitute(
                ExternalCategoryMappingId.of(2L),
                DEFAULT_EXTERNAL_SOURCE_ID,
                "CAT_INACTIVE",
                "비활성 카테고리",
                200L,
                ExternalCategoryMappingStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
