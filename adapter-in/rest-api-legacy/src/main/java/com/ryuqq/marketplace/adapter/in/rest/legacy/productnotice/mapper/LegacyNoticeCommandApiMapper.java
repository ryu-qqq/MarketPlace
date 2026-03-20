package com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacy.notice.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 고시정보 Request → 표준 UpdateProductNoticeCommand 변환 매퍼.
 *
 * <p>레거시 flat 필드(material, color, size...) + NoticeCategory를 받아서
 * 표준 (noticeCategoryId + entries) 구조로 변환합니다.
 */
@Component
public class LegacyNoticeCommandApiMapper {

    private static final Logger log = LoggerFactory.getLogger(LegacyNoticeCommandApiMapper.class);
    private static final String DEFAULT_NOTICE_VALUE = "상세설명 참고";

    private record FieldMapping(
            String fieldCode,
            Function<LegacyCreateProductNoticeRequest, String> extractor) {}

    private static final List<FieldMapping> FIELD_MAPPINGS =
            List.of(
                    new FieldMapping("material", LegacyCreateProductNoticeRequest::material),
                    new FieldMapping("color", LegacyCreateProductNoticeRequest::color),
                    new FieldMapping("size", LegacyCreateProductNoticeRequest::size),
                    new FieldMapping("manufacturer", LegacyCreateProductNoticeRequest::maker),
                    new FieldMapping("made_in", r -> resolveOriginDescription(r.origin())),
                    new FieldMapping("wash_care", LegacyCreateProductNoticeRequest::washingMethod),
                    new FieldMapping("release_date", LegacyCreateProductNoticeRequest::yearMonth),
                    new FieldMapping(
                            "quality_assurance",
                            LegacyCreateProductNoticeRequest::assuranceStandard),
                    new FieldMapping("cs_info", LegacyCreateProductNoticeRequest::asPhone));

    /** 레거시 요청 → LegacyUpdateNoticeCommand (luxurydb 저장용). */
    public LegacyUpdateNoticeCommand toLegacyNoticeCommand(
            long productGroupId, LegacyCreateProductNoticeRequest request) {
        return new LegacyUpdateNoticeCommand(
                productGroupId,
                nullToEmpty(request.material()),
                nullToEmpty(request.color()),
                nullToEmpty(request.size()),
                nullToEmpty(request.maker()),
                nullToEmpty(request.origin()),
                nullToEmpty(request.washingMethod()),
                nullToEmpty(request.yearMonth()),
                nullToEmpty(request.assuranceStandard()),
                nullToEmpty(request.asPhone()));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * 레거시 요청 + NoticeCategory → 표준 UpdateProductNoticeCommand 변환.
     * 새 스키마 전환 시 사용 예정.
     *
     * @param productGroupId 상품그룹 ID
     * @param request 레거시 고시정보 요청 (flat 필드)
     * @param noticeCategory 해석된 고시정보 카테고리 (필드 포함)
     * @return 표준 고시정보 수정 커맨드
     */
    public UpdateProductNoticeCommand toUpdateNoticeCommand(
            long productGroupId,
            LegacyCreateProductNoticeRequest request,
            NoticeCategory noticeCategory) {

        Map<String, String> legacyValues = extractLegacyValues(request, noticeCategory);
        List<UpdateProductNoticeCommand.NoticeEntryCommand> entries = new ArrayList<>();

        for (NoticeField field : noticeCategory.fields()) {
            String fieldCode = field.fieldCodeValue();
            String value = legacyValues.getOrDefault(fieldCode, DEFAULT_NOTICE_VALUE);
            entries.add(
                    new UpdateProductNoticeCommand.NoticeEntryCommand(field.idValue(), value));
        }

        return new UpdateProductNoticeCommand(
                productGroupId, noticeCategory.idValue(), entries);
    }

    private Map<String, String> extractLegacyValues(
            LegacyCreateProductNoticeRequest request, NoticeCategory noticeCategory) {
        Map<String, String> categoryFieldCodes = new LinkedHashMap<>();
        for (NoticeField field : noticeCategory.fields()) {
            categoryFieldCodes.put(field.fieldCodeValue(), field.fieldCodeValue());
        }

        Map<String, String> values = new LinkedHashMap<>();
        for (FieldMapping mapping : FIELD_MAPPINGS) {
            if (!categoryFieldCodes.containsKey(mapping.fieldCode())) {
                continue;
            }
            String value = mapping.extractor().apply(request);
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
