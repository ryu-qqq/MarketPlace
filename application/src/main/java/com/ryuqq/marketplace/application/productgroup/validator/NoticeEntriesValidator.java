package com.ryuqq.marketplace.application.productgroup.validator;

import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.exception.NoticeInvalidFieldException;
import com.ryuqq.marketplace.domain.notice.exception.NoticeRequiredFieldMissingException;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeEntries;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * 고시정보 entries 검증기.
 *
 * <p>카테고리 필드 일치 + 필수 필드 존재를 단일 패스로 검증합니다.
 */
@Component
public class NoticeEntriesValidator {

    private final NoticeCategoryReadManager noticeCategoryReadManager;

    public NoticeEntriesValidator(NoticeCategoryReadManager noticeCategoryReadManager) {
        this.noticeCategoryReadManager = noticeCategoryReadManager;
    }

    /**
     * 고시정보 entries를 검증합니다.
     *
     * <p>1. entries의 noticeFieldId가 해당 카테고리에 실제 존재하는지 검증
     *
     * <p>2. 카테고리의 필수 필드가 모두 포함되었는지 검증
     *
     * @param noticeEntries 검증 대상 고시정보 entries
     */
    public void validate(ProductNoticeEntries noticeEntries) {
        NoticeCategory noticeCategory =
                noticeCategoryReadManager.getById(noticeEntries.noticeCategoryId());
        List<NoticeField> categoryFields = noticeCategory.fields();

        // 카테고리 필드 기준: 유효 필드 ID + 필수 필드 ID 를 단일 패스로 구성
        Set<Long> validFieldIds = new HashSet<>(categoryFields.size());
        Set<Long> requiredFieldIds = new HashSet<>();
        for (NoticeField field : categoryFields) {
            validFieldIds.add(field.idValue());
            if (field.isRequired()) {
                requiredFieldIds.add(field.idValue());
            }
        }

        // entries 단일 패스: 유효하지 않은 필드 수집 + 필수 필드 제거
        List<Long> invalidFieldIds = new ArrayList<>();
        for (ProductNoticeEntry entry : noticeEntries.toList()) {
            Long fieldId = entry.noticeFieldIdValue();
            if (!validFieldIds.contains(fieldId)) {
                invalidFieldIds.add(fieldId);
            }
            requiredFieldIds.remove(fieldId);
        }

        if (!invalidFieldIds.isEmpty()) {
            throw new NoticeInvalidFieldException(invalidFieldIds);
        }

        if (!requiredFieldIds.isEmpty()) {
            throw new NoticeRequiredFieldMissingException(new ArrayList<>(requiredFieldIds));
        }
    }
}
