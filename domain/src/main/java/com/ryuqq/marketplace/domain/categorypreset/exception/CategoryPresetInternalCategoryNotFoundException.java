package com.ryuqq.marketplace.domain.categorypreset.exception;

import java.util.List;

/** 요청한 내부 카테고리가 존재하지 않을 때 예외. */
public class CategoryPresetInternalCategoryNotFoundException extends CategoryPresetException {

    private static final CategoryPresetErrorCode ERROR_CODE =
            CategoryPresetErrorCode.CATEGORY_PRESET_INTERNAL_CATEGORY_NOT_FOUND;

    public CategoryPresetInternalCategoryNotFoundException(List<Long> missingIds) {
        super(ERROR_CODE, String.format("존재하지 않는 내부 카테고리 ID: %s", missingIds));
    }
}
