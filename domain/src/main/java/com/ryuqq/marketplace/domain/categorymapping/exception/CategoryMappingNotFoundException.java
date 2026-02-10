package com.ryuqq.marketplace.domain.categorymapping.exception;

/** 카테고리 매핑을 찾을 수 없을 때 예외. */
public class CategoryMappingNotFoundException extends CategoryMappingException {

    private static final CategoryMappingErrorCode ERROR_CODE =
            CategoryMappingErrorCode.CATEGORY_MAPPING_NOT_FOUND;

    public CategoryMappingNotFoundException() {
        super(ERROR_CODE);
    }

    public CategoryMappingNotFoundException(Long categoryMappingId) {
        super(ERROR_CODE, String.format("카테고리 매핑을 찾을 수 없습니다 (id: %d)", categoryMappingId));
    }
}
