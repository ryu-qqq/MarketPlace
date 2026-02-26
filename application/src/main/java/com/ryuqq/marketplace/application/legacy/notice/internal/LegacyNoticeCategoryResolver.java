package com.ryuqq.marketplace.application.legacy.notice.internal;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품의 NoticeCategory를 해석하고 인메모리 캐시를 제공한다.
 *
 * <p>CategoryGroup → NoticeCategory 매핑은 거의 변하지 않는 참조 데이터이므로 ConcurrentHashMap으로 캐시하여 반복 DB 조회를
 * 방지합니다.
 *
 * <p>productGroupId 기반 조회: productGroupId → ProductGroup → categoryId → Category → categoryGroup →
 * NoticeCategory
 *
 * <p>categoryId 기반 조회: categoryId → Category → categoryGroup → NoticeCategory (최초 등록 시 사용)
 */
@Component
public class LegacyNoticeCategoryResolver {

    private final ProductGroupReadManager productGroupReadManager;
    private final CategoryReadManager categoryReadManager;
    private final NoticeCategoryReadManager noticeCategoryReadManager;

    private final ConcurrentHashMap<CategoryGroup, NoticeCategory> cache =
            new ConcurrentHashMap<>();

    public LegacyNoticeCategoryResolver(
            ProductGroupReadManager productGroupReadManager,
            CategoryReadManager categoryReadManager,
            NoticeCategoryReadManager noticeCategoryReadManager) {
        this.productGroupReadManager = productGroupReadManager;
        this.categoryReadManager = categoryReadManager;
        this.noticeCategoryReadManager = noticeCategoryReadManager;
    }

    /** 내부 productGroupId 기반 NoticeCategory 조회 (개별 수정 시 사용). */
    public NoticeCategory resolveByProductGroupId(long productGroupId) {
        ProductGroup productGroup =
                productGroupReadManager.getById(ProductGroupId.of(productGroupId));
        return resolveByCategoryGroup(
                categoryReadManager.getById(productGroup.categoryId()).categoryGroup());
    }

    private NoticeCategory resolveByCategoryGroup(CategoryGroup categoryGroup) {
        return cache.computeIfAbsent(categoryGroup, noticeCategoryReadManager::getByCategoryGroup);
    }
}
