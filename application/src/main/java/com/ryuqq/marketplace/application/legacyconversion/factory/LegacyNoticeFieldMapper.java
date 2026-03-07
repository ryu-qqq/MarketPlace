package com.ryuqq.marketplace.application.legacyconversion.factory;

import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 레거시 고시정보 고정 컬럼 → 내부 notice_field.field_code 매핑 유틸리티.
 *
 * <p>매핑 정의의 단일 소스(Single Source of Truth)로, Create/Update Factory 모두 이 클래스를 사용합니다.
 */
final class LegacyNoticeFieldMapper {

    private static final Logger log = LoggerFactory.getLogger(LegacyNoticeFieldMapper.class);

    private LegacyNoticeFieldMapper() {}

    private static final List<FieldMapping> FIELD_MAPPINGS =
            List.of(
                    new FieldMapping(
                            "material", LegacyProductGroupCompositeResult.NoticeInfo::material),
                    new FieldMapping("color", LegacyProductGroupCompositeResult.NoticeInfo::color),
                    new FieldMapping("size", LegacyProductGroupCompositeResult.NoticeInfo::size),
                    new FieldMapping(
                            "manufacturer", LegacyProductGroupCompositeResult.NoticeInfo::maker),
                    new FieldMapping("made_in", n -> resolveOriginDescription(n.origin())),
                    new FieldMapping(
                            "wash_care",
                            LegacyProductGroupCompositeResult.NoticeInfo::washingMethod),
                    new FieldMapping(
                            "release_date",
                            LegacyProductGroupCompositeResult.NoticeInfo::yearMonthDay),
                    new FieldMapping(
                            "quality_assurance",
                            LegacyProductGroupCompositeResult.NoticeInfo::assuranceStandard),
                    new FieldMapping(
                            "cs_info", LegacyProductGroupCompositeResult.NoticeInfo::asPhone));

    private record FieldMapping(
            String fieldCode,
            Function<LegacyProductGroupCompositeResult.NoticeInfo, String> extractor) {}

    /**
     * 레거시 NoticeInfo에서 카테고리에 존재하는 field_code에 해당하는 값만 추출합니다.
     *
     * @param noticeInfo 레거시 고시정보 (null 허용)
     * @param noticeCategory 대상 고시 카테고리
     * @return field_code → 값 맵 (빈 값은 제외)
     */
    static Map<String, String> extractLegacyValues(
            LegacyProductGroupCompositeResult.NoticeInfo noticeInfo,
            NoticeCategory noticeCategory) {
        if (noticeInfo == null) {
            return Map.of();
        }

        Set<String> categoryFieldCodes = new LinkedHashSet<>();
        for (NoticeField field : noticeCategory.fields()) {
            categoryFieldCodes.add(field.fieldCodeValue());
        }

        Map<String, String> values = new LinkedHashMap<>();
        for (FieldMapping mapping : FIELD_MAPPINGS) {
            if (!categoryFieldCodes.contains(mapping.fieldCode())) {
                continue;
            }
            String value = mapping.extractor().apply(noticeInfo);
            if (value != null && !value.isBlank()) {
                values.put(mapping.fieldCode(), value);
            }
        }
        return values;
    }

    private static String resolveOriginDescription(String originCode) {
        if (originCode == null || originCode.isBlank()) {
            return null;
        }
        try {
            return Origin.valueOf(originCode).description();
        } catch (IllegalArgumentException e) {
            log.warn("알 수 없는 원산지 코드: {}. 원본 값을 그대로 사용합니다.", originCode);
            return originCode;
        }
    }
}
