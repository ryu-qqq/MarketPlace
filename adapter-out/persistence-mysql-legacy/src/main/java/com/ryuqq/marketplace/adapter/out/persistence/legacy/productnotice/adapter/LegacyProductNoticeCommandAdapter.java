package com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.repository.LegacyProductNoticeJpaRepository;
import com.ryuqq.marketplace.application.legacy.productnotice.port.out.command.LegacyProductNoticeCommandPort;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 고시정보 저장 Adapter.
 *
 * <p>표준 커맨드의 entries를 NoticeCategory의 fieldCode로 역매핑하여 luxurydb flat 컬럼에 저장합니다.
 */
@Component
public class LegacyProductNoticeCommandAdapter implements LegacyProductNoticeCommandPort {

    private final LegacyProductNoticeJpaRepository repository;

    public LegacyProductNoticeCommandAdapter(LegacyProductNoticeJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void update(UpdateProductNoticeCommand command, NoticeCategory noticeCategory) {
        Map<String, String> fieldCodeToValue = buildFieldCodeValueMap(command, noticeCategory);

        LegacyProductNoticeEntity entity =
                LegacyProductNoticeEntity.create(
                        command.productGroupId(),
                        fieldCodeToValue.getOrDefault("material", ""),
                        fieldCodeToValue.getOrDefault("color", ""),
                        fieldCodeToValue.getOrDefault("size", ""),
                        fieldCodeToValue.getOrDefault("manufacturer", ""),
                        fieldCodeToValue.getOrDefault("made_in", ""),
                        fieldCodeToValue.getOrDefault("wash_care", ""),
                        fieldCodeToValue.getOrDefault("release_date", ""),
                        fieldCodeToValue.getOrDefault("quality_assurance", ""),
                        fieldCodeToValue.getOrDefault("cs_info", ""));

        repository.save(entity);
    }

    private Map<String, String> buildFieldCodeValueMap(
            UpdateProductNoticeCommand command, NoticeCategory noticeCategory) {
        Map<Long, String> fieldIdToCode = new HashMap<>();
        for (NoticeField field : noticeCategory.fields()) {
            fieldIdToCode.put(field.idValue(), field.fieldCodeValue());
        }

        Map<String, String> result = new HashMap<>();
        for (UpdateProductNoticeCommand.NoticeEntryCommand entry : command.entries()) {
            String fieldCode = fieldIdToCode.get(entry.noticeFieldId());
            if (fieldCode != null) {
                result.put(fieldCode, entry.fieldValue());
            }
        }
        return result;
    }
}
