package com.ryuqq.marketplace.domain.notice.aggregate;

import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldName;

/** 고시정보 필드 (제조국, 제조자, 소재 등). */
public class NoticeField {

    private final NoticeFieldId id;
    private final NoticeFieldCode fieldCode;
    private NoticeFieldName fieldName;
    private boolean required;
    private int sortOrder;

    private NoticeField(
            NoticeFieldId id,
            NoticeFieldCode fieldCode,
            NoticeFieldName fieldName,
            boolean required,
            int sortOrder) {
        this.id = id;
        this.fieldCode = fieldCode;
        this.fieldName = fieldName;
        this.required = required;
        this.sortOrder = sortOrder;
    }

    /** 신규 고시정보 필드 생성. */
    public static NoticeField forNew(
            NoticeFieldCode fieldCode, NoticeFieldName fieldName, boolean required, int sortOrder) {
        return new NoticeField(NoticeFieldId.forNew(), fieldCode, fieldName, required, sortOrder);
    }

    /** 영속성에서 복원 시 사용. */
    public static NoticeField reconstitute(
            NoticeFieldId id,
            NoticeFieldCode fieldCode,
            NoticeFieldName fieldName,
            boolean required,
            int sortOrder) {
        return new NoticeField(id, fieldCode, fieldName, required, sortOrder);
    }

    /** 필드 정보 수정. */
    public void update(NoticeFieldName fieldName, boolean required, int sortOrder) {
        this.fieldName = fieldName;
        this.required = required;
        this.sortOrder = sortOrder;
    }

    public NoticeFieldId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public NoticeFieldCode fieldCode() {
        return fieldCode;
    }

    public String fieldCodeValue() {
        return fieldCode.value();
    }

    public NoticeFieldName fieldName() {
        return fieldName;
    }

    public String fieldNameValue() {
        return fieldName.value();
    }

    public boolean isRequired() {
        return required;
    }

    public int sortOrder() {
        return sortOrder;
    }
}
