package com.ryuqq.marketplace.domain.categorypreset.exception;

/** 카테고리 프리셋을 찾을 수 없을 때 예외. */
public class CategoryPresetNotFoundException extends CategoryPresetException {

    private static final CategoryPresetErrorCode ERROR_CODE =
            CategoryPresetErrorCode.CATEGORY_PRESET_NOT_FOUND;

    public CategoryPresetNotFoundException() {
        super(ERROR_CODE);
    }

    public CategoryPresetNotFoundException(Long categoryPresetId) {
        super(ERROR_CODE, String.format("카테고리 프리셋을 찾을 수 없습니다 (id: %d)", categoryPresetId));
    }
}
