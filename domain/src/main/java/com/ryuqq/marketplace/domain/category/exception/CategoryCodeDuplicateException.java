package com.ryuqq.marketplace.domain.category.exception;

/** 카테고리 코드 중복 예외. */
public class CategoryCodeDuplicateException extends CategoryException {

    private static final CategoryErrorCode ERROR_CODE = CategoryErrorCode.CATEGORY_CODE_DUPLICATE;

    public CategoryCodeDuplicateException() {
        super(ERROR_CODE);
    }

    public CategoryCodeDuplicateException(String code) {
        super(ERROR_CODE, String.format("카테고리 코드 '%s'가 이미 존재합니다", code));
    }
}
