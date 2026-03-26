package com.ryuqq.marketplace.application.legacy.productcontext.resolver;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 카테고리 ID → NoticeCategory 리졸버.
 *
 * <p>레거시 categoryId → internalCategoryId → NoticeCategory 해석. 매핑 실패 시 CLOTHING 카테고리를 기본값으로 사용합니다.
 */
@Component
public class LegacyNoticeCategoryResolver {

    private static final Logger log = LoggerFactory.getLogger(LegacyNoticeCategoryResolver.class);
    private static final long SETOF_SOURCE_ID = 2L;
    private static final CategoryGroup DEFAULT_CATEGORY_GROUP = CategoryGroup.CLOTHING;

    private final InboundProductMappingResolver mappingResolver;
    private final CategoryNoticeResolver categoryNoticeResolver;
    private final NoticeCategoryReadManager noticeCategoryReadManager;

    public LegacyNoticeCategoryResolver(
            InboundProductMappingResolver mappingResolver,
            CategoryNoticeResolver categoryNoticeResolver,
            NoticeCategoryReadManager noticeCategoryReadManager) {
        this.mappingResolver = mappingResolver;
        this.categoryNoticeResolver = categoryNoticeResolver;
        this.noticeCategoryReadManager = noticeCategoryReadManager;
    }

    public NoticeCategory resolve(long legacyCategoryId) {
        Optional<Long> internalCategoryId =
                mappingResolver.resolveInternalCategoryId(
                        SETOF_SOURCE_ID, String.valueOf(legacyCategoryId));

        Optional<NoticeCategory> resolved =
                internalCategoryId.flatMap(categoryNoticeResolver::resolve);

        if (resolved.isPresent()) {
            return resolved.get();
        }

        log.warn(
                "레거시 카테고리 → NoticeCategory 매핑 실패. 기본 카테고리 사용: legacyCategoryId={}, default={}",
                legacyCategoryId,
                DEFAULT_CATEGORY_GROUP);

        return noticeCategoryReadManager.getByCategoryGroup(DEFAULT_CATEGORY_GROUP);
    }
}
