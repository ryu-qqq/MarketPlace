package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.condition.ExternalCategoryMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.ExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.QExternalCategoryMappingJpaEntity;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
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

    public List<ExternalCategoryMappingJpaEntity> findByExternalSourceId(Long externalSourceId) {
        return queryFactory
                .selectFrom(externalCategoryMapping)
                .where(externalCategoryMapping.externalSourceId.eq(externalSourceId))
                .fetch();
    }

    public List<ExternalCategoryMappingJpaEntity> findByCriteria(
            ExternalCategoryMappingSearchParams params) {
        return queryFactory
                .selectFrom(externalCategoryMapping)
                .where(
                        conditionBuilder.externalSourceIdEq(params),
                        conditionBuilder.statusIn(params),
                        conditionBuilder.searchCondition(params))
                .offset(params.offset())
                .limit(params.size())
                .fetch();
    }

    public long countByCriteria(ExternalCategoryMappingSearchParams params) {
        Long count =
                queryFactory
                        .select(externalCategoryMapping.count())
                        .from(externalCategoryMapping)
                        .where(
                                conditionBuilder.externalSourceIdEq(params),
                                conditionBuilder.statusIn(params),
                                conditionBuilder.searchCondition(params))
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
