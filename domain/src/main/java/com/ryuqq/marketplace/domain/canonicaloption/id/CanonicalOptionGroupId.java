package com.ryuqq.marketplace.domain.canonicaloption.id;

/** 캐노니컬 옵션 그룹 ID Value Object. */
public record CanonicalOptionGroupId(Long value) {

    public static CanonicalOptionGroupId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CanonicalOptionGroupId 값은 null일 수 없습니다");
        }
        return new CanonicalOptionGroupId(value);
    }
}
