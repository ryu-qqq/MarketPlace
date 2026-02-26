package com.ryuqq.marketplace.domain.notice.id;

/** 고시정보 필드 ID Value Object. */
public record NoticeFieldId(Long value) {

    public static NoticeFieldId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("NoticeFieldId 값은 null일 수 없습니다");
        }
        return new NoticeFieldId(value);
    }

    public static NoticeFieldId forNew() {
        return new NoticeFieldId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
