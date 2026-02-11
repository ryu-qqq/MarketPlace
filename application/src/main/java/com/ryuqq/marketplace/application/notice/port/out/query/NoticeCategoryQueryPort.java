package com.ryuqq.marketplace.application.notice.port.out.query;

import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import java.util.Optional;

/** 고시정보 카테고리 Query Port. */
public interface NoticeCategoryQueryPort {
    Optional<NoticeCategory> findById(NoticeCategoryId id);

    Optional<NoticeCategory> findByCategoryGroup(CategoryGroup categoryGroup);

    List<NoticeCategory> findByCriteria(NoticeCategorySearchCriteria criteria);

    long countByCriteria(NoticeCategorySearchCriteria criteria);
}
