package com.ryuqq.marketplace.domain.legacy.notice.vo;

import java.util.Map;

/**
 * 레거시 고시정보 고정 필드 ↔ notice_field 매핑.
 *
 * <p>레거시 API의 고정 9개 필드를 표준 notice_field의 field_code와 매핑합니다. notice_category_id = 100
 * (LEGACY_DEFAULT) 기준입니다.
 */
public final class LegacyNoticeFieldMapping {

    public static final long LEGACY_NOTICE_CATEGORY_ID = 100L;

    /** field_code → notice_field_id 매핑. */
    public static final Map<String, Long> FIELD_CODE_TO_ID =
            Map.of(
                    "material", 100L,
                    "color", 101L,
                    "size", 102L,
                    "maker", 103L,
                    "origin", 104L,
                    "washingMethod", 105L,
                    "yearMonth", 106L,
                    "assuranceStandard", 107L,
                    "asPhone", 108L);

    /** notice_field_id → field_code 역매핑. */
    public static final Map<Long, String> ID_TO_FIELD_CODE =
            Map.of(
                    100L, "material",
                    101L, "color",
                    102L, "size",
                    103L, "maker",
                    104L, "origin",
                    105L, "washingMethod",
                    106L, "yearMonth",
                    107L, "assuranceStandard",
                    108L, "asPhone");

    private LegacyNoticeFieldMapping() {}
}
