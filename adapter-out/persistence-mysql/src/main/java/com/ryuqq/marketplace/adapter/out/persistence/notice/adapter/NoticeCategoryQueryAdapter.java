package com.ryuqq.marketplace.adapter.out.persistence.notice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.notice.mapper.NoticeCategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeCategoryQueryDslRepository;
import com.ryuqq.marketplace.application.notice.port.out.query.NoticeCategoryQueryPort;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** NoticeCategory Query Adapter. */
@Component
public class NoticeCategoryQueryAdapter implements NoticeCategoryQueryPort {

    private final NoticeCategoryQueryDslRepository queryDslRepository;
    private final NoticeCategoryJpaEntityMapper mapper;

    public NoticeCategoryQueryAdapter(
            NoticeCategoryQueryDslRepository queryDslRepository,
            NoticeCategoryJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<NoticeCategory> findById(NoticeCategoryId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<NoticeCategory> findByCategoryGroup(CategoryGroup categoryGroup) {
        return queryDslRepository
                .findByTargetCategoryGroup(categoryGroup.name())
                .map(mapper::toDomain);
    }

    @Override
    public List<NoticeCategory> findByCriteria(NoticeCategorySearchCriteria criteria) {
        return queryDslRepository.findByCriteria(criteria).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByCriteria(NoticeCategorySearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }
}
