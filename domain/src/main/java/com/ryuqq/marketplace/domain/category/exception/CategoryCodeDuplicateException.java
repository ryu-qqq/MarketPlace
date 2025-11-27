package com.ryuqq.marketplace.domain.category.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * Category Code Duplicate Exception
 *
 * <p>카테고리 코드가 중복될 때 발생하는 예외</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryCodeDuplicateException extends DomainException {

    /**
     * 중복된 카테고리 코드
     *
     * @param code 카테고리 코드
     */
    public CategoryCodeDuplicateException(String code) {
        super(
            CategoryErrorCode.CATEGORY_CODE_DUPLICATE.getCode(),
            CategoryErrorCode.CATEGORY_CODE_DUPLICATE.getMessage(),
            Map.of("code", code)
        );
    }
}
