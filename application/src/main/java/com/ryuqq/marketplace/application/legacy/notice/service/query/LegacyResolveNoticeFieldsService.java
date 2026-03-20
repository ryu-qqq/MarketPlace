package com.ryuqq.marketplace.application.legacy.notice.service.query;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import com.ryuqq.marketplace.application.legacy.notice.port.in.query.LegacyResolveNoticeFieldsUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품그룹의 고시정보 카테고리 해석 서비스.
 *
 * <p>레거시 productGroupId → 레거시 categoryId → 내부 categoryId → NoticeCategory 해석.
 * 매핑 실패 시 CLOTHING 카테고리를 기본값으로 사용합니다.
 */
@Service
public class LegacyResolveNoticeFieldsService implements LegacyResolveNoticeFieldsUseCase {

    private static final Logger log = LoggerFactory.getLogger(LegacyResolveNoticeFieldsService.class);
    private static final long SETOF_SOURCE_ID = 2L;
    private static final CategoryGroup DEFAULT_CATEGORY_GROUP = CategoryGroup.CLOTHING;

    private final LegacyProductGroupReadManager legacyProductGroupReadManager;
    private final InboundProductMappingResolver mappingResolver;
    private final CategoryNoticeResolver categoryNoticeResolver;
    private final NoticeCategoryReadManager noticeCategoryReadManager;

    public LegacyResolveNoticeFieldsService(
            LegacyProductGroupReadManager legacyProductGroupReadManager,
            InboundProductMappingResolver mappingResolver,
            CategoryNoticeResolver categoryNoticeResolver,
            NoticeCategoryReadManager noticeCategoryReadManager) {
        this.legacyProductGroupReadManager = legacyProductGroupReadManager;
        this.mappingResolver = mappingResolver;
        this.categoryNoticeResolver = categoryNoticeResolver;
        this.noticeCategoryReadManager = noticeCategoryReadManager;
    }

    @Override
    public NoticeCategory execute(long legacyProductGroupId) {
        LegacyProductGroup productGroup =
                legacyProductGroupReadManager.getById(
                        LegacyProductGroupId.of(legacyProductGroupId));

        long legacyCategoryId = productGroup.categoryId();

        Optional<Long> internalCategoryId =
                mappingResolver.resolveInternalCategoryId(
                        SETOF_SOURCE_ID, String.valueOf(legacyCategoryId));

        Optional<NoticeCategory> resolved =
                internalCategoryId.flatMap(categoryNoticeResolver::resolve);

        if (resolved.isPresent()) {
            return resolved.get();
        }

        log.warn(
                "레거시 카테고리 → NoticeCategory 매핑 실패. 기본 카테고리 사용: "
                        + "legacyProductGroupId={}, legacyCategoryId={}, default={}",
                legacyProductGroupId,
                legacyCategoryId,
                DEFAULT_CATEGORY_GROUP);

        return noticeCategoryReadManager.getByCategoryGroup(DEFAULT_CATEGORY_GROUP);
    }
}
