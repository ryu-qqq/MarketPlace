package com.ryuqq.marketplace.domain.category.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * Category Has Children Exception
 *
 * <p>하위 카테고리가 있어 삭제할 수 없을 때 발생하는 예외</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryHasChildrenException extends DomainException {

    /**
     * 하위 카테고리가 있는 경우
     *
     * @param categoryId 카테고리 ID
     */
    public CategoryHasChildrenException(Long categoryId) {
        super(
            CategoryErrorCode.CATEGORY_HAS_CHILDREN.getCode(),
            CategoryErrorCode.CATEGORY_HAS_CHILDREN.getMessage(),
            Map.of("categoryId", categoryId)
        );
    }
}
