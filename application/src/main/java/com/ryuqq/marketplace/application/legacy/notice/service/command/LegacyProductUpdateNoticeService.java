package com.ryuqq.marketplace.application.legacy.notice.service.command;

import com.ryuqq.marketplace.application.legacy.notice.internal.LegacyNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.notice.port.in.command.LegacyProductUpdateNoticeUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 고시정보 수정 서비스.
 *
 * <p>표준 커맨드의 entries를 레거시 flat 필드로 역매핑하여 luxurydb에 저장합니다.
 */
@Service
public class LegacyProductUpdateNoticeService implements LegacyProductUpdateNoticeUseCase {

    private final LegacyNoticeCommandCoordinator noticeCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateNoticeService(
            LegacyNoticeCommandCoordinator noticeCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(UpdateProductNoticeCommand command, NoticeCategory noticeCategory) {
        Map<String, String> fieldCodeToValue = buildFieldCodeValueMap(command, noticeCategory);

        LegacyProductNotice notice =
                new LegacyProductNotice(
                        fieldCodeToValue.getOrDefault("material", ""),
                        fieldCodeToValue.getOrDefault("color", ""),
                        fieldCodeToValue.getOrDefault("size", ""),
                        fieldCodeToValue.getOrDefault("manufacturer", ""),
                        fieldCodeToValue.getOrDefault("made_in", ""),
                        fieldCodeToValue.getOrDefault("wash_care", ""),
                        fieldCodeToValue.getOrDefault("release_date", ""),
                        fieldCodeToValue.getOrDefault("quality_assurance", ""),
                        fieldCodeToValue.getOrDefault("cs_info", ""));

        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());
        noticeCommandCoordinator.update(groupId, notice, Instant.now());
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
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
