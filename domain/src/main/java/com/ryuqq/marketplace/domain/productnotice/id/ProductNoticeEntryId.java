package com.ryuqq.marketplace.domain.productnotice.id;

/** ProductNoticeEntry ID Value Object. */
public record ProductNoticeEntryId(Long value) {

    public static ProductNoticeEntryId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ProductNoticeEntryId 값은 null일 수 없습니다");
        }
        return new ProductNoticeEntryId(value);
    }

    public static ProductNoticeEntryId forNew() {
        return new ProductNoticeEntryId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
