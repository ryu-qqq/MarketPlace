package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OriginAreaInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.ProductInfoProvidedNotice;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeEntryResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 상품정보제공고시(Notice) 및 원산지 변환 매퍼. */
final class NaverNoticeMapper {

    static final String ORIGIN_AREA_IMPORT = "03";
    static final String ORIGIN_AREA_CONTENT = "상세설명에 표시";
    private static final String DEFAULT_NOTICE_TYPE = "ETC";
    private static final String MADE_IN_FIELD_CODE = "made_in";
    private static final String NOTICE_DEFAULT_VALUE = "상품 상세 참조";

    /** 우리 NoticeCategory.code -> 네이버 productInfoProvidedNoticeType 매핑. */
    private static final Map<String, String> NAVER_NOTICE_TYPE_MAP =
            Map.ofEntries(
                    Map.entry("CLOTHING", "WEAR"),
                    Map.entry("SHOES", "SHOES"),
                    Map.entry("BAGS", "BAG"),
                    Map.entry("ACCESSORIES", "FASHION_ITEMS"),
                    Map.entry("COSMETICS", "ETC"),
                    Map.entry("JEWELRY", "ETC"),
                    Map.entry("WATCHES", "ETC"),
                    Map.entry("FURNITURE", "FURNITURE"),
                    Map.entry("BABY_KIDS", "KIDS"),
                    Map.entry("SPORTS", "ETC"),
                    Map.entry("DIGITAL", "ETC"),
                    Map.entry("ETC", "ETC"));

    private static final Map<String, String> ETC_FIELD_MAP =
            Map.of(
                    "product_name", "itemName",
                    "model_name", "modelName",
                    "certification", "certificateDetails",
                    "manufacturer", "manufacturer",
                    "quality_assurance", "qualityAssuranceStandard");

    private static final Map<String, String> WEAR_FIELD_MAP =
            Map.of(
                    "material", "material",
                    "color", "color",
                    "size", "size",
                    "manufacturer", "manufacturer",
                    "product_name", "manufacturer",
                    "caution", "caution");

    private static final Map<String, String> SHOES_FIELD_MAP =
            Map.of(
                    "material_upper", "material",
                    "material_sole", "material",
                    "color", "color",
                    "size", "size",
                    "manufacturer", "manufacturer",
                    "caution", "caution");

    private static final Map<String, String> BAG_FIELD_MAP =
            Map.of(
                    "type", "type",
                    "material", "material",
                    "color", "color",
                    "size", "size",
                    "manufacturer", "manufacturer");

    private static final Map<String, String> FASHION_ITEMS_FIELD_MAP =
            Map.of(
                    "type", "type",
                    "material", "material",
                    "size", "size",
                    "manufacturer", "manufacturer",
                    "care_instructions", "caution");

    private static final Map<String, String> FURNITURE_FIELD_MAP =
            Map.ofEntries(
                    Map.entry("product_name", "itemName"),
                    Map.entry("material", "material"),
                    Map.entry("color", "color"),
                    Map.entry("size", "size"),
                    Map.entry("manufacturer", "producer"),
                    Map.entry("certification", "certificationType"),
                    Map.entry("delivery", "installedCharge"));

    private static final Map<String, String> KIDS_FIELD_MAP =
            Map.ofEntries(
                    Map.entry("product_name", "itemName"),
                    Map.entry("model", "modelName"),
                    Map.entry("material", "material"),
                    Map.entry("color", "color"),
                    Map.entry("size", "size"),
                    Map.entry("manufacturer", "manufacturer"),
                    Map.entry("caution", "caution"),
                    Map.entry("age_range", "recommendedAge"),
                    Map.entry("weight", "weight"),
                    Map.entry("certification", "certificationType"));

    private NaverNoticeMapper() {}

    static ProductInfoProvidedNotice mapNotice(ProductGroupSyncData syncData) {
        if (syncData.notice().isEmpty()) {
            return null;
        }

        ProductNoticeResult notice = syncData.notice().get();
        NoticeCategoryResult category = syncData.noticeCategory().orElse(null);

        String noticeType = resolveNaverNoticeType(category);

        Map<Long, String> fieldCodeMap = buildFieldCodeMap(category);
        Map<Long, String> fieldNameMap = buildFieldNameMap(category);

        List<ProductNoticeEntryResult> entries = notice.entries();
        if (entries.isEmpty()) {
            return null;
        }

        Map<String, String> contents =
                buildNoticeContents(noticeType, entries, fieldCodeMap, fieldNameMap);

        return ProductInfoProvidedNotice.of(noticeType, contents);
    }

    static OriginAreaInfo mapOriginAreaInfo(ProductGroupSyncData syncData) {
        if (syncData.notice().isEmpty() || syncData.noticeCategory().isEmpty()) {
            return new OriginAreaInfo(ORIGIN_AREA_IMPORT, ORIGIN_AREA_CONTENT);
        }

        NoticeCategoryResult category = syncData.noticeCategory().get();
        Long madeInFieldId = null;
        for (NoticeFieldResult field : category.fields()) {
            if (MADE_IN_FIELD_CODE.equals(field.fieldCode())) {
                madeInFieldId = field.id();
                break;
            }
        }

        if (madeInFieldId == null) {
            return new OriginAreaInfo(ORIGIN_AREA_IMPORT, ORIGIN_AREA_CONTENT);
        }

        ProductNoticeResult notice = syncData.notice().get();
        for (ProductNoticeEntryResult entry : notice.entries()) {
            if (madeInFieldId.equals(entry.noticeFieldId())) {
                String originValue = entry.fieldValue();
                if (originValue != null && !originValue.isBlank()) {
                    return new OriginAreaInfo(ORIGIN_AREA_IMPORT, originValue);
                }
                break;
            }
        }

        return new OriginAreaInfo(ORIGIN_AREA_IMPORT, ORIGIN_AREA_CONTENT);
    }

    private static Map<String, String> buildNoticeContents(
            String noticeType,
            List<ProductNoticeEntryResult> entries,
            Map<Long, String> fieldCodeMap,
            Map<Long, String> fieldNameMap) {

        Map<String, String> typeFieldMap = resolveTypeFieldMap(noticeType);
        Map<String, String> contents = new LinkedHashMap<>();

        contents.put("returnCostReason", NOTICE_DEFAULT_VALUE);
        contents.put("noRefundReason", NOTICE_DEFAULT_VALUE);
        contents.put("qualityAssuranceStandard", NOTICE_DEFAULT_VALUE);
        contents.put("compensationProcedure", NOTICE_DEFAULT_VALUE);
        contents.put("troubleShootingContents", NOTICE_DEFAULT_VALUE);

        for (ProductNoticeEntryResult entry : entries) {
            String fieldCode = fieldCodeMap.get(entry.noticeFieldId());
            String value = entry.fieldValue();

            if (value == null || value.isBlank()) {
                value = NOTICE_DEFAULT_VALUE;
            }

            if (fieldCode != null && typeFieldMap.containsKey(fieldCode)) {
                String naverField = typeFieldMap.get(fieldCode);
                contents.put(naverField, value);
            }
        }

        ensureRequiredFields(noticeType, contents);

        return contents;
    }

    private static Map<String, String> resolveTypeFieldMap(String noticeType) {
        return switch (noticeType) {
            case "WEAR" -> WEAR_FIELD_MAP;
            case "SHOES" -> SHOES_FIELD_MAP;
            case "BAG" -> BAG_FIELD_MAP;
            case "FASHION_ITEMS" -> FASHION_ITEMS_FIELD_MAP;
            case "FURNITURE" -> FURNITURE_FIELD_MAP;
            case "KIDS" -> KIDS_FIELD_MAP;
            default -> ETC_FIELD_MAP;
        };
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private static void ensureRequiredFields(String noticeType, Map<String, String> contents) {
        String packDate = java.time.YearMonth.now().toString();

        switch (noticeType) {
            case "ETC" -> ensureEtcDefaults(contents);
            case "WEAR" -> {
                contents.putIfAbsent("material", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("color", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("size", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("manufacturer", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("caution", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("packDate", packDate);
                contents.putIfAbsent("warrantyPolicy", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("afterServiceDirector", NOTICE_DEFAULT_VALUE);
            }
            case "SHOES" -> {
                contents.putIfAbsent("material", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("color", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("size", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("height", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("manufacturer", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("caution", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("packDate", packDate);
                contents.putIfAbsent("warrantyPolicy", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("afterServiceDirector", NOTICE_DEFAULT_VALUE);
            }
            case "BAG" -> {
                contents.putIfAbsent("type", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("material", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("color", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("size", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("manufacturer", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("caution", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("packDate", packDate);
                contents.putIfAbsent("warrantyPolicy", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("afterServiceDirector", NOTICE_DEFAULT_VALUE);
            }
            case "FASHION_ITEMS" -> {
                contents.putIfAbsent("type", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("material", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("size", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("manufacturer", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("caution", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("packDate", packDate);
                contents.putIfAbsent("warrantyPolicy", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("afterServiceDirector", NOTICE_DEFAULT_VALUE);
            }
            case "FURNITURE" -> {
                contents.putIfAbsent("itemName", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("material", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("color", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("size", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("components", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("producer", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("manufacturer", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("certificationType", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("installedCharge", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("warrantyPolicy", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("afterServiceDirector", NOTICE_DEFAULT_VALUE);
            }
            case "KIDS" -> {
                contents.putIfAbsent("itemName", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("modelName", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("material", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("color", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("size", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("weight", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("recommendedAge", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("releaseDate", java.time.YearMonth.now().toString());
                contents.putIfAbsent("manufacturer", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("certificationType", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("caution", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("warrantyPolicy", NOTICE_DEFAULT_VALUE);
                contents.putIfAbsent("afterServiceDirector", NOTICE_DEFAULT_VALUE);
            }
            default -> ensureEtcDefaults(contents);
        }
    }

    private static void ensureEtcDefaults(Map<String, String> contents) {
        contents.putIfAbsent("itemName", NOTICE_DEFAULT_VALUE);
        contents.putIfAbsent("modelName", NOTICE_DEFAULT_VALUE);
        contents.putIfAbsent("manufacturer", NOTICE_DEFAULT_VALUE);
        contents.putIfAbsent("afterServiceDirector", NOTICE_DEFAULT_VALUE);
    }

    private static String resolveNaverNoticeType(NoticeCategoryResult category) {
        if (category == null) {
            return DEFAULT_NOTICE_TYPE;
        }
        return NAVER_NOTICE_TYPE_MAP.getOrDefault(category.code(), DEFAULT_NOTICE_TYPE);
    }

    private static Map<Long, String> buildFieldCodeMap(NoticeCategoryResult category) {
        if (category == null) {
            return Map.of();
        }
        Map<Long, String> map = new HashMap<>();
        for (NoticeFieldResult field : category.fields()) {
            map.put(field.id(), field.fieldCode());
        }
        return map;
    }

    private static Map<Long, String> buildFieldNameMap(NoticeCategoryResult category) {
        if (category == null) {
            return Map.of();
        }
        Map<Long, String> map = new HashMap<>();
        for (NoticeFieldResult field : category.fields()) {
            map.put(field.id(), field.fieldName());
        }
        return map;
    }
}
