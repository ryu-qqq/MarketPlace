package com.ryuqq.marketplace.domain.canonicaloption.exception;

/** 캐노니컬 옵션 그룹을 찾을 수 없을 때 예외. */
public class CanonicalOptionGroupNotFoundException extends CanonicalOptionException {

    private static final CanonicalOptionErrorCode ERROR_CODE =
            CanonicalOptionErrorCode.CANONICAL_OPTION_GROUP_NOT_FOUND;

    public CanonicalOptionGroupNotFoundException() {
        super(ERROR_CODE);
    }

    public CanonicalOptionGroupNotFoundException(Long canonicalOptionGroupId) {
        super(ERROR_CODE,
                String.format("캐노니컬 옵션 그룹을 찾을 수 없습니다 (id: %d)", canonicalOptionGroupId));
    }
}
