package com.ryuqq.marketplace.application.legacy.productnotice.manager;

import com.ryuqq.marketplace.application.legacy.productnotice.port.out.command.LegacyProductNoticeCommandPort;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품 고시정보 저장 매니저. */
@Component
public class LegacyProductNoticeCommandManager {

    private final LegacyProductNoticeCommandPort commandPort;
    private final NoticeCategoryReadManager noticeCategoryReadManager;

    public LegacyProductNoticeCommandManager(
            LegacyProductNoticeCommandPort commandPort,
            NoticeCategoryReadManager noticeCategoryReadManager) {
        this.commandPort = commandPort;
        this.noticeCategoryReadManager = noticeCategoryReadManager;
    }

    @Transactional
    public void register(RegisterProductNoticeCommand command) {
        NoticeCategory noticeCategory =
                noticeCategoryReadManager.getById(
                        NoticeCategoryId.of(command.noticeCategoryId()));

        Map<Long, String> entries = new HashMap<>();
        for (RegisterProductNoticeCommand.NoticeEntryCommand entry : command.entries()) {
            entries.put(entry.noticeFieldId(), entry.fieldValue());
        }

        commandPort.persist(command.productGroupId(), toFlatFields(noticeCategory, entries));
    }

    @Transactional
    public void update(UpdateProductNoticeCommand command, NoticeCategory noticeCategory) {
        Map<Long, String> entries = new HashMap<>();
        for (UpdateProductNoticeCommand.NoticeEntryCommand entry : command.entries()) {
            entries.put(entry.noticeFieldId(), entry.fieldValue());
        }

        commandPort.persist(command.productGroupId(), toFlatFields(noticeCategory, entries));
    }

    private Map<String, String> toFlatFields(
            NoticeCategory noticeCategory, Map<Long, String> fieldIdToValue) {
        Map<String, String> flatFields = new HashMap<>();
        for (NoticeField field : noticeCategory.fields()) {
            String value = fieldIdToValue.get(field.idValue());
            if (value != null) {
                flatFields.put(field.fieldCodeValue(), value);
            }
        }
        return flatFields;
    }
}
