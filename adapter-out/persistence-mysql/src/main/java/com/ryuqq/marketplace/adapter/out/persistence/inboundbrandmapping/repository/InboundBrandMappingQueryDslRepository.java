package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.condition.InboundBrandMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.QInboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** InboundBrandMapping QueryDSL Repository. */
@Repository
public class InboundBrandMappingQueryDslRepository {

    private static final QInboundBrandMappingJpaEntity inboundBrandMapping =
            QInboundBrandMappingJpaEntity.inboundBrandMappingJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final InboundBrandMappingConditionBuilder conditionBuilder;

    public InboundBrandMappingQueryDslRepository(
            JPAQueryFactory queryFactory, InboundBrandMappingConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<InboundBrandMappingJpaEntity> findById(Long id) {
        InboundBrandMappingJpaEntity entity =
                queryFactory
                        .selectFrom(inboundBrandMapping)
                        .where(inboundBrandMapping.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<InboundBrandMappingJpaEntity> findByInboundSourceIdAndExternalBrandCode(
            Long inboundSourceId, String externalBrandCode) {
        InboundBrandMappingJpaEntity entity =
                queryFactory
                        .selectFrom(inboundBrandMapping)
                        .where(
                                inboundBrandMapping.inboundSourceId.eq(inboundSourceId),
                                inboundBrandMapping.externalBrandCode.eq(externalBrandCode))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<InboundBrandMappingJpaEntity> findByInboundSourceIdAndExternalBrandCodes(
            Long inboundSourceId, List<String> externalBrandCodes) {
        return queryFactory
                .selectFrom(inboundBrandMapping)
                .where(
                        inboundBrandMapping.inboundSourceId.eq(inboundSourceId),
                        inboundBrandMapping.externalBrandCode.in(externalBrandCodes))
                .fetch();
    }

    public List<InboundBrandMappingJpaEntity> findByInboundSourceId(Long inboundSourceId) {
        return queryFactory
                .selectFrom(inboundBrandMapping)
                .where(inboundBrandMapping.inboundSourceId.eq(inboundSourceId))
                .fetch();
    }

    public List<InboundBrandMappingJpaEntity> findByCriteria(
            InboundBrandMappingSearchCriteria criteria) {
        return queryFactory
                .selectFrom(inboundBrandMapping)
                .where(
                        conditionBuilder.inboundSourceIdEq(criteria.inboundSourceId()),
                        conditionBuilder.statusIn(criteria.statusNames()),
                        conditionBuilder.searchCondition(
                                criteria.searchField(), criteria.searchWord()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(InboundBrandMappingSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(inboundBrandMapping.count())
                        .from(inboundBrandMapping)
                        .where(
                                conditionBuilder.inboundSourceIdEq(criteria.inboundSourceId()),
                                conditionBuilder.statusIn(criteria.statusNames()),
                                conditionBuilder.searchCondition(
                                        criteria.searchField(), criteria.searchWord()))
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
