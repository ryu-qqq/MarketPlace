package com.ryuqq.marketplace.domain.notice.id;

/** 고시정보 카테고리 ID Value Object. */
public record NoticeCategoryId(Long value) {

    public static NoticeCategoryId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("NoticeCategoryId 값은 null일 수 없습니다");
        }
        return new NoticeCategoryId(value);
    }

    public static NoticeCategoryId forNew() {
        return new NoticeCategoryId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
