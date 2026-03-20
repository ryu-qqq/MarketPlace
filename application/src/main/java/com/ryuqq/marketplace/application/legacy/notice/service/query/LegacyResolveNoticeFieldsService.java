package com.ryuqq.marketplace.application.legacy.notice.service.query;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import com.ryuqq.marketplace.application.legacy.notice.port.in.query.LegacyResolveNoticeFieldsUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품그룹의 고시정보 카테고리 해석 서비스.
 *
 * <p>레거시 productGroupId → 레거시 categoryId → 내부 categoryId → NoticeCategory 해석.
 */
@Service
public class LegacyResolveNoticeFieldsService implements LegacyResolveNoticeFieldsUseCase {

    private static final long SETOF_SOURCE_ID = 2L;

    private final LegacyProductGroupReadManager legacyProductGroupReadManager;
    private final InboundProductMappingResolver mappingResolver;
    private final CategoryNoticeResolver categoryNoticeResolver;

    public LegacyResolveNoticeFieldsService(
            LegacyProductGroupReadManager legacyProductGroupReadManager,
            InboundProductMappingResolver mappingResolver,
            CategoryNoticeResolver categoryNoticeResolver) {
        this.legacyProductGroupReadManager = legacyProductGroupReadManager;
        this.mappingResolver = mappingResolver;
        this.categoryNoticeResolver = categoryNoticeResolver;
    }

    @Override
    public Optional<NoticeCategory> execute(long legacyProductGroupId) {
        LegacyProductGroup productGroup =
                legacyProductGroupReadManager.getById(
                        LegacyProductGroupId.of(legacyProductGroupId));

        long legacyCategoryId = productGroup.categoryId();

        Optional<Long> internalCategoryId =
                mappingResolver.resolveInternalCategoryId(
                        SETOF_SOURCE_ID, String.valueOf(legacyCategoryId));

        return internalCategoryId.flatMap(categoryNoticeResolver::resolve);
    }
}
