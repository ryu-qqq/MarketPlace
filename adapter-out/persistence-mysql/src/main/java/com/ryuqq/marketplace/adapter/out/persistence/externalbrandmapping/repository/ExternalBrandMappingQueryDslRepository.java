package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.condition.ExternalBrandMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity.ExternalBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity.QExternalBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** ExternalBrandMapping QueryDSL Repository. */
@Repository
public class ExternalBrandMappingQueryDslRepository {

    private static final QExternalBrandMappingJpaEntity externalBrandMapping =
            QExternalBrandMappingJpaEntity.externalBrandMappingJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ExternalBrandMappingConditionBuilder conditionBuilder;

    public ExternalBrandMappingQueryDslRepository(
            JPAQueryFactory queryFactory, ExternalBrandMappingConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ExternalBrandMappingJpaEntity> findById(Long id) {
        ExternalBrandMappingJpaEntity entity =
                queryFactory
                        .selectFrom(externalBrandMapping)
                        .where(externalBrandMapping.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<ExternalBrandMappingJpaEntity> findByExternalSourceIdAndExternalBrandCode(
            Long externalSourceId, String externalBrandCode) {
        ExternalBrandMappingJpaEntity entity =
                queryFactory
                        .selectFrom(externalBrandMapping)
                        .where(
                                externalBrandMapping.externalSourceId.eq(externalSourceId),
                                externalBrandMapping.externalBrandCode.eq(externalBrandCode))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ExternalBrandMappingJpaEntity> findByExternalSourceIdAndExternalBrandCodes(
            Long externalSourceId, List<String> externalBrandCodes) {
        return queryFactory
                .selectFrom(externalBrandMapping)
                .where(
                        externalBrandMapping.externalSourceId.eq(externalSourceId),
                        externalBrandMapping.externalBrandCode.in(externalBrandCodes))
                .fetch();
    }

    public List<ExternalBrandMappingJpaEntity> findByExternalSourceId(Long externalSourceId) {
        return queryFactory
                .selectFrom(externalBrandMapping)
                .where(externalBrandMapping.externalSourceId.eq(externalSourceId))
                .fetch();
    }

    public List<ExternalBrandMappingJpaEntity> findByCriteria(
            ExternalBrandMappingSearchCriteria criteria) {
        return queryFactory
                .selectFrom(externalBrandMapping)
                .where(
                        conditionBuilder.externalSourceIdEq(criteria.externalSourceId()),
                        conditionBuilder.statusIn(criteria.statusNames()),
                        conditionBuilder.searchCondition(
                                criteria.searchField(), criteria.searchWord()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ExternalBrandMappingSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(externalBrandMapping.count())
                        .from(externalBrandMapping)
                        .where(
                                conditionBuilder.externalSourceIdEq(criteria.externalSourceId()),
                                conditionBuilder.statusIn(criteria.statusNames()),
                                conditionBuilder.searchCondition(
                                        criteria.searchField(), criteria.searchWord()))
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
