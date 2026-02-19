package com.ryuqq.marketplace.domain.externalsource.id;

/** ExternalSource ID Value Object. */
public record ExternalSourceId(Long value) {

    public static ExternalSourceId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExternalSourceId 값은 null일 수 없습니다");
        }
        return new ExternalSourceId(value);
    }

    public static ExternalSourceId forNew() {
        return new ExternalSourceId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
