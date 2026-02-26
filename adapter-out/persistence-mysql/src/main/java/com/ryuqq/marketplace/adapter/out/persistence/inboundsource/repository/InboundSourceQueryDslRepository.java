package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.condition.InboundSourceConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.QInboundSourceJpaEntity;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** InboundSource QueryDSL Repository. */
@Repository
public class InboundSourceQueryDslRepository {

    private static final QInboundSourceJpaEntity inboundSource =
            QInboundSourceJpaEntity.inboundSourceJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final InboundSourceConditionBuilder conditionBuilder;

    public InboundSourceQueryDslRepository(
            JPAQueryFactory queryFactory, InboundSourceConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<InboundSourceJpaEntity> findById(Long id) {
        InboundSourceJpaEntity entity =
                queryFactory.selectFrom(inboundSource).where(inboundSource.id.eq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<InboundSourceJpaEntity> findByCode(String code) {
        InboundSourceJpaEntity entity =
                queryFactory
                        .selectFrom(inboundSource)
                        .where(inboundSource.code.eq(code))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<InboundSourceJpaEntity> findByCriteria(InboundSourceSearchCriteria criteria) {
        return queryFactory
                .selectFrom(inboundSource)
                .where(
                        conditionBuilder.typeIn(criteria.typeNames()),
                        conditionBuilder.statusIn(criteria.statusNames()),
                        conditionBuilder.searchCondition(
                                criteria.searchField(), criteria.searchWord()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(InboundSourceSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(inboundSource.count())
                        .from(inboundSource)
                        .where(
                                conditionBuilder.typeIn(criteria.typeNames()),
                                conditionBuilder.statusIn(criteria.statusNames()),
                                conditionBuilder.searchCondition(
                                        criteria.searchField(), criteria.searchWord()))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsByCode(String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(inboundSource)
                        .where(inboundSource.code.eq(code))
                        .fetchFirst();
        return result != null;
    }
}
