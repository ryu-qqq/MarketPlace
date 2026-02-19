package com.ryuqq.marketplace.adapter.out.persistence.notice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.mapper.NoticeCategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeCategoryQueryDslRepository;
import com.ryuqq.marketplace.application.notice.port.out.query.NoticeCategoryQueryPort;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * NoticeCategory Query Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>Aggregate Root를 통해 자식(NoticeField)까지 완전히 로딩합니다.
 */
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
        return queryDslRepository
                .findById(id.value())
                .map(
                        entity -> {
                            List<NoticeFieldJpaEntity> fields =
                                    queryDslRepository.findFieldsByCategoryId(entity.getId());
                            return mapper.toDomain(entity, fields);
                        });
    }

    @Override
    public Optional<NoticeCategory> findByCategoryGroup(CategoryGroup categoryGroup) {
        return queryDslRepository
                .findByTargetCategoryGroup(categoryGroup.name())
                .map(
                        entity -> {
                            List<NoticeFieldJpaEntity> fields =
                                    queryDslRepository.findFieldsByCategoryId(entity.getId());
                            return mapper.toDomain(entity, fields);
                        });
    }

    @Override
    public List<NoticeCategory> findByCriteria(NoticeCategorySearchCriteria criteria) {
        List<NoticeCategoryJpaEntity> categories = queryDslRepository.findByCriteria(criteria);
        if (categories.isEmpty()) {
            return List.of();
        }

        List<Long> categoryIds = categories.stream().map(NoticeCategoryJpaEntity::getId).toList();

        Map<Long, List<NoticeFieldJpaEntity>> fieldsMap =
                queryDslRepository.findFieldsByCategoryIds(categoryIds).stream()
                        .collect(Collectors.groupingBy(NoticeFieldJpaEntity::getNoticeCategoryId));

        return categories.stream()
                .map(
                        category ->
                                mapper.toDomain(
                                        category,
                                        fieldsMap.getOrDefault(category.getId(), List.of())))
                .toList();
    }

    @Override
    public long countByCriteria(NoticeCategorySearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }
}
