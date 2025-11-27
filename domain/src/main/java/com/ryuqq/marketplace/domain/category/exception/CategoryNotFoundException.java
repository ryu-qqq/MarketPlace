package com.ryuqq.marketplace.domain.category.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * Category Not Found Exception
 *
 * <p>카테고리를 찾을 수 없을 때 발생하는 예외</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryNotFoundException extends DomainException {

    /**
     * ID로 카테고리를 찾을 수 없는 경우
     *
     * @param categoryId 카테고리 ID
     */
    public CategoryNotFoundException(Long categoryId) {
        super(
            CategoryErrorCode.CATEGORY_NOT_FOUND.getCode(),
            CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage(),
            Map.of("categoryId", categoryId)
        );
    }

    /**
     * 코드로 카테고리를 찾을 수 없는 경우
     *
     * @param code 카테고리 코드
     */
    public CategoryNotFoundException(String code) {
        super(
            CategoryErrorCode.CATEGORY_NOT_FOUND.getCode(),
            CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage(),
            Map.of("code", code)
        );
    }
}
