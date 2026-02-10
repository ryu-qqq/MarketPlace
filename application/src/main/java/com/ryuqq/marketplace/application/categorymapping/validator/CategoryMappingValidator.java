package com.ryuqq.marketplace.application.categorymapping.validator;

import com.ryuqq.marketplace.application.categorymapping.manager.CategoryMappingReadManager;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.exception.CategoryMappingDuplicateException;
import com.ryuqq.marketplace.domain.categorymapping.exception.CategoryMappingNotFoundException;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import org.springframework.stereotype.Component;

/**
 * CategoryMapping Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class CategoryMappingValidator {

    private final CategoryMappingReadManager readManager;

    public CategoryMappingValidator(CategoryMappingReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 카테고리 매핑 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 카테고리 매핑 ID
     * @return CategoryMapping 도메인 객체
     * @throws CategoryMappingNotFoundException 존재하지 않는 경우
     */
    public CategoryMapping findExistingOrThrow(CategoryMappingId id) {
        return readManager.getById(id);
    }

    /**
     * 해당 외부 카테고리에 대한 매핑 중복 여부 검증.
     *
     * @param salesChannelCategoryId 외부 채널 카테고리 ID
     * @throws CategoryMappingDuplicateException 이미 매핑이 존재하는 경우
     */
    public void validateNotDuplicate(Long salesChannelCategoryId) {
        if (readManager.existsBySalesChannelCategoryId(salesChannelCategoryId)) {
            throw new CategoryMappingDuplicateException(salesChannelCategoryId);
        }
    }
}
