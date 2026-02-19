package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.condition.ExternalCategoryMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.ExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.QExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** ExternalCategoryMapping QueryDSL Repository. */
@Repository
public class ExternalCategoryMappingQueryDslRepository {

    private static final QExternalCategoryMappingJpaEntity externalCategoryMapping =
            QExternalCategoryMappingJpaEntity.externalCategoryMappingJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ExternalCategoryMappingConditionBuilder conditionBuilder;

    public ExternalCategoryMappingQueryDslRepository(
            JPAQueryFactory queryFactory,
            ExternalCategoryMappingConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ExternalCategoryMappingJpaEntity> findById(Long id) {
        ExternalCategoryMappingJpaEntity entity =
                queryFactory
                        .selectFrom(externalCategoryMapping)
                        .where(externalCategoryMapping.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<ExternalCategoryMappingJpaEntity> findByExternalSourceIdAndExternalCategoryCode(
            Long externalSourceId, String externalCategoryCode) {
        ExternalCategoryMappingJpaEntity entity =
                queryFactory
                        .selectFrom(externalCategoryMapping)
                        .where(
                                externalCategoryMapping.externalSourceId.eq(externalSourceId),
                                externalCategoryMapping.externalCategoryCode.eq(
                                        externalCategoryCode))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ExternalCategoryMappingJpaEntity> findByExternalSourceIdAndExternalCategoryCodes(
            Long externalSourceId, List<String> externalCategoryCodes) {
        return queryFactory
                .selectFrom(externalCategoryMapping)
                .where(
                        externalCategoryMapping.externalSourceId.eq(externalSourceId),
                        externalCategoryMapping.externalCategoryCode.in(externalCategoryCodes))
                .fetch();
    }

    public List<ExternalCategoryMappingJpaEntity> findByExternalSourceId(Long externalSourceId) {
        return queryFactory
                .selectFrom(externalCategoryMapping)
                .where(externalCategoryMapping.externalSourceId.eq(externalSourceId))
                .fetch();
    }

    public List<ExternalCategoryMappingJpaEntity> findByCriteria(
            ExternalCategoryMappingSearchCriteria criteria) {
        return queryFactory
                .selectFrom(externalCategoryMapping)
                .where(
                        conditionBuilder.externalSourceIdEq(criteria.externalSourceId()),
                        conditionBuilder.statusIn(criteria.statusNames()),
                        conditionBuilder.searchCondition(
                                criteria.searchField(), criteria.searchWord()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ExternalCategoryMappingSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(externalCategoryMapping.count())
                        .from(externalCategoryMapping)
                        .where(
                                conditionBuilder.externalSourceIdEq(criteria.externalSourceId()),
                                conditionBuilder.statusIn(criteria.statusNames()),
                                conditionBuilder.searchCondition(
                                        criteria.searchField(), criteria.searchWord()))
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
