package com.ryuqq.marketplace.application.productnotice.validator;

import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.exception.NoticeInvalidFieldException;
import com.ryuqq.marketplace.domain.notice.exception.NoticeRequiredFieldMissingException;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeEntries;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeUpdateData;
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
     * ProductNotice 도메인 객체로 검증합니다.
     *
     * @param productNotice 검증 대상 고시정보
     */
    public void validate(ProductNotice productNotice) {
        validateEntries(productNotice.noticeCategoryId(), productNotice.entries());
    }

    /**
     * ProductNoticeEntries VO로 검증합니다.
     *
     * @param noticeEntries 검증 대상 고시정보 entries
     */
    public void validate(ProductNoticeEntries noticeEntries) {
        validateEntries(noticeEntries.noticeCategoryId(), noticeEntries.toList());
    }

    /**
     * ProductNoticeUpdateData로 검증합니다.
     *
     * @param updateData 검증 대상 수정 데이터
     */
    public void validate(ProductNoticeUpdateData updateData) {
        validateEntries(updateData.noticeCategoryId(), updateData.entries());
    }

    private void validateEntries(
            com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId noticeCategoryId,
            List<ProductNoticeEntry> entries) {
        NoticeCategory noticeCategory = noticeCategoryReadManager.getById(noticeCategoryId);
        List<NoticeField> categoryFields = noticeCategory.fields();

        Set<Long> validFieldIds = new HashSet<>(categoryFields.size());
        Set<Long> requiredFieldIds = new HashSet<>();
        for (NoticeField field : categoryFields) {
            validFieldIds.add(field.idValue());
            if (field.isRequired()) {
                requiredFieldIds.add(field.idValue());
            }
        }

        List<Long> invalidFieldIds = new ArrayList<>();
        for (ProductNoticeEntry entry : entries) {
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
