package com.ryuqq.marketplace.application.notice.resolver;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.exception.NoticeCategoryNotFoundException;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 상품 카테고리 ID를 기반으로 고시정보 카테고리를 해석하는 리졸버.
 *
 * <p>상품 카테고리의 {@link CategoryGroup}을 조회한 뒤, 해당 그룹에 매핑된 {@link NoticeCategory}를 반환합니다. 매칭되는 고시정보
 * 카테고리가 없으면 빈 Optional을 반환합니다.
 */
@Component
public class CategoryNoticeResolver {

    private final CategoryReadManager categoryReadManager;
    private final NoticeCategoryReadManager noticeCategoryReadManager;

    public CategoryNoticeResolver(
            CategoryReadManager categoryReadManager,
            NoticeCategoryReadManager noticeCategoryReadManager) {
        this.categoryReadManager = categoryReadManager;
        this.noticeCategoryReadManager = noticeCategoryReadManager;
    }

    /**
     * 상품 카테고리 ID로부터 고시정보 카테고리를 해석합니다.
     *
     * @param internalCategoryId 내부 상품 카테고리 ID
     * @return 매칭되는 고시정보 카테고리, 없으면 빈 Optional
     */
    public Optional<NoticeCategory> resolve(long internalCategoryId) {
        CategoryGroup categoryGroup =
                categoryReadManager.getById(CategoryId.of(internalCategoryId)).categoryGroup();
        try {
            return Optional.of(noticeCategoryReadManager.getByCategoryGroup(categoryGroup));
        } catch (NoticeCategoryNotFoundException e) {
            return Optional.empty();
        }
    }
}
