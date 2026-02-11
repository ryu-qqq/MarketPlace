package com.ryuqq.marketplace.domain.canonicaloption.id;

/** 캐노니컬 옵션 값 ID Value Object. */
public record CanonicalOptionValueId(Long value) {

    public static CanonicalOptionValueId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CanonicalOptionValueId 값은 null일 수 없습니다");
        }
        return new CanonicalOptionValueId(value);
    }
}
