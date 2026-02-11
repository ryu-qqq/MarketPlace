package com.ryuqq.marketplace.domain.productnotice.aggregate;

import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeEntryId;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;

/**
 * 고시정보 항목 (Child Entity of ProductNotice).
 * 개별 NoticeField에 대한 값을 보관한다.
 */
public class ProductNoticeEntry {

    private final ProductNoticeEntryId id;
    private final NoticeFieldId noticeFieldId;
    private NoticeFieldValue fieldValue;

    private ProductNoticeEntry(
            ProductNoticeEntryId id,
            NoticeFieldId noticeFieldId,
            NoticeFieldValue fieldValue) {
        this.id = id;
        this.noticeFieldId = noticeFieldId;
        this.fieldValue = fieldValue;
    }

    /** 신규 고시정보 항목 생성. */
    public static ProductNoticeEntry forNew(
            NoticeFieldId noticeFieldId,
            NoticeFieldValue fieldValue) {
        return new ProductNoticeEntry(
                ProductNoticeEntryId.forNew(),
                noticeFieldId,
                fieldValue);
    }

    /** 영속성에서 복원 시 사용. */
    public static ProductNoticeEntry reconstitute(
            ProductNoticeEntryId id,
            NoticeFieldId noticeFieldId,
            NoticeFieldValue fieldValue) {
        return new ProductNoticeEntry(id, noticeFieldId, fieldValue);
    }

    /** 값 수정. */
    public void updateValue(NoticeFieldValue fieldValue) {
        this.fieldValue = fieldValue;
    }

    public ProductNoticeEntryId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public NoticeFieldId noticeFieldId() {
        return noticeFieldId;
    }

    public Long noticeFieldIdValue() {
        return noticeFieldId.value();
    }

    public NoticeFieldValue fieldValue() {
        return fieldValue;
    }

    public String fieldValueValue() {
        return fieldValue.value();
    }
}
