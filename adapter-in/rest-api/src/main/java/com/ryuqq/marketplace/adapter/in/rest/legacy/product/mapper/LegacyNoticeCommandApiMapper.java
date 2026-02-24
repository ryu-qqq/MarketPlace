package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateNoticeCommand;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 고지 Request → LegacyUpdateNoticeCommand 변환 매퍼.
 *
 * <p>필드명 → 필드코드 매핑만 수행하며, noticeCategoryId 및 noticeFieldId 해석은 LegacyNoticeUpdateCoordinator에서 내부
 * productGroupId 기반으로 수행합니다.
 */
@Component
public class LegacyNoticeCommandApiMapper {

    private static final String NOTICE_FIELD_MATERIAL = "material";
    private static final String NOTICE_FIELD_COLOR = "color";
    private static final String NOTICE_FIELD_SIZE = "size";
    private static final String NOTICE_FIELD_MANUFACTURER = "manufacturer";
    private static final String NOTICE_FIELD_MADE_IN = "made_in";
    private static final String NOTICE_FIELD_WASH_CARE = "wash_care";
    private static final String NOTICE_FIELD_RELEASE_DATE = "release_date";
    private static final String NOTICE_FIELD_QUALITY_ASSURANCE = "quality_assurance";

    /**
     * LegacyCreateProductNoticeRequest → LegacyUpdateNoticeCommand.
     *
     * <p>필드명 → 필드코드 매핑만 수행하며, noticeCategoryId 및 noticeFieldId 해석은 LegacyNoticeUpdateCoordinator에서
     * 내부 productGroupId 기반으로 수행합니다.
     */
    public LegacyUpdateNoticeCommand toLegacyNoticeCommand(
            long setofProductGroupId, LegacyCreateProductNoticeRequest request) {
        Map<String, String> noticeFields = new LinkedHashMap<>();
        putIfPresent(noticeFields, NOTICE_FIELD_MATERIAL, request.material());
        putIfPresent(noticeFields, NOTICE_FIELD_COLOR, request.color());
        putIfPresent(noticeFields, NOTICE_FIELD_SIZE, request.size());
        putIfPresent(noticeFields, NOTICE_FIELD_MANUFACTURER, request.maker());
        putIfPresent(noticeFields, NOTICE_FIELD_MADE_IN, request.origin());
        putIfPresent(noticeFields, NOTICE_FIELD_WASH_CARE, request.washingMethod());
        putIfPresent(noticeFields, NOTICE_FIELD_RELEASE_DATE, request.yearMonth());
        putIfPresent(noticeFields, NOTICE_FIELD_QUALITY_ASSURANCE, request.assuranceStandard());
        return new LegacyUpdateNoticeCommand(setofProductGroupId, noticeFields);
    }

    private void putIfPresent(Map<String, String> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }
}
