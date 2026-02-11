package com.ryuqq.marketplace.application.notice.manager;

import com.ryuqq.marketplace.application.notice.port.out.query.NoticeCategoryQueryPort;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.exception.NoticeCategoryNotFoundException;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 고시정보 카테고리 Read Manager. */
@Component
public class NoticeCategoryReadManager {

    private final NoticeCategoryQueryPort queryPort;

    public NoticeCategoryReadManager(NoticeCategoryQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public NoticeCategory getById(NoticeCategoryId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new NoticeCategoryNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public NoticeCategory getByCategoryGroup(CategoryGroup categoryGroup) {
        return queryPort
                .findByCategoryGroup(categoryGroup)
                .orElseThrow(NoticeCategoryNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<NoticeCategory> findByCriteria(NoticeCategorySearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(NoticeCategorySearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
