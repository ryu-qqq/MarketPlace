package com.ryuqq.marketplace.domain.categorymapping.exception;

/** 해당 외부 카테고리에 이미 매핑이 존재할 때 예외. */
public class CategoryMappingDuplicateException extends CategoryMappingException {

    private static final CategoryMappingErrorCode ERROR_CODE =
            CategoryMappingErrorCode.CATEGORY_MAPPING_DUPLICATE;

    public CategoryMappingDuplicateException() {
        super(ERROR_CODE);
    }

    public CategoryMappingDuplicateException(Long salesChannelCategoryId) {
        super(ERROR_CODE, String.format("외부 카테고리 ID '%d'에 이미 매핑이 존재합니다", salesChannelCategoryId));
    }
}
