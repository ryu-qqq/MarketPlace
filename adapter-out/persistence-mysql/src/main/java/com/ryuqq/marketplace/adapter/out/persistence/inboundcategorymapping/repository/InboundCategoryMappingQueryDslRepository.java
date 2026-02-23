package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.condition.InboundCategoryMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.QInboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** InboundCategoryMapping QueryDSL Repository. */
@Repository
public class InboundCategoryMappingQueryDslRepository {

    private static final QInboundCategoryMappingJpaEntity inboundCategoryMapping =
            QInboundCategoryMappingJpaEntity.inboundCategoryMappingJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final InboundCategoryMappingConditionBuilder conditionBuilder;

    public InboundCategoryMappingQueryDslRepository(
            JPAQueryFactory queryFactory, InboundCategoryMappingConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<InboundCategoryMappingJpaEntity> findById(Long id) {
        InboundCategoryMappingJpaEntity entity =
                queryFactory
                        .selectFrom(inboundCategoryMapping)
                        .where(inboundCategoryMapping.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<InboundCategoryMappingJpaEntity> findByInboundSourceIdAndExternalCategoryCode(
            Long inboundSourceId, String externalCategoryCode) {
        InboundCategoryMappingJpaEntity entity =
                queryFactory
                        .selectFrom(inboundCategoryMapping)
                        .where(
                                inboundCategoryMapping.inboundSourceId.eq(inboundSourceId),
                                inboundCategoryMapping.externalCategoryCode.eq(
                                        externalCategoryCode))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<InboundCategoryMappingJpaEntity> findByInboundSourceIdAndExternalCategoryCodes(
            Long inboundSourceId, List<String> externalCategoryCodes) {
        return queryFactory
                .selectFrom(inboundCategoryMapping)
                .where(
                        inboundCategoryMapping.inboundSourceId.eq(inboundSourceId),
                        inboundCategoryMapping.externalCategoryCode.in(externalCategoryCodes))
                .fetch();
    }

    public List<InboundCategoryMappingJpaEntity> findByInboundSourceId(Long inboundSourceId) {
        return queryFactory
                .selectFrom(inboundCategoryMapping)
                .where(inboundCategoryMapping.inboundSourceId.eq(inboundSourceId))
                .fetch();
    }

    public List<InboundCategoryMappingJpaEntity> findByCriteria(
            InboundCategoryMappingSearchCriteria criteria) {
        return queryFactory
                .selectFrom(inboundCategoryMapping)
                .where(
                        conditionBuilder.inboundSourceIdEq(criteria.inboundSourceId()),
                        conditionBuilder.statusIn(criteria.statusNames()),
                        conditionBuilder.searchCondition(
                                criteria.searchField(), criteria.searchWord()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(InboundCategoryMappingSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(inboundCategoryMapping.count())
                        .from(inboundCategoryMapping)
                        .where(
                                conditionBuilder.inboundSourceIdEq(criteria.inboundSourceId()),
                                conditionBuilder.statusIn(criteria.statusNames()),
                                conditionBuilder.searchCondition(
                                        criteria.searchField(), criteria.searchWord()))
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
