package com.ryuqq.marketplace.adapter.out.persistence.externalsource.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.condition.ExternalSourceConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.entity.ExternalSourceJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalsource.entity.QExternalSourceJpaEntity;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** ExternalSource QueryDSL Repository. */
@Repository
public class ExternalSourceQueryDslRepository {

    private static final QExternalSourceJpaEntity externalSource =
            QExternalSourceJpaEntity.externalSourceJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ExternalSourceConditionBuilder conditionBuilder;

    public ExternalSourceQueryDslRepository(
            JPAQueryFactory queryFactory, ExternalSourceConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ExternalSourceJpaEntity> findById(Long id) {
        ExternalSourceJpaEntity entity =
                queryFactory.selectFrom(externalSource).where(externalSource.id.eq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<ExternalSourceJpaEntity> findByCode(String code) {
        ExternalSourceJpaEntity entity =
                queryFactory
                        .selectFrom(externalSource)
                        .where(externalSource.code.eq(code))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ExternalSourceJpaEntity> findByCriteria(ExternalSourceSearchParams params) {
        return queryFactory
                .selectFrom(externalSource)
                .where(
                        conditionBuilder.typeIn(params),
                        conditionBuilder.statusIn(params),
                        conditionBuilder.searchCondition(params))
                .offset(params.offset())
                .limit(params.size())
                .fetch();
    }

    public long countByCriteria(ExternalSourceSearchParams params) {
        Long count =
                queryFactory
                        .select(externalSource.count())
                        .from(externalSource)
                        .where(
                                conditionBuilder.typeIn(params),
                                conditionBuilder.statusIn(params),
                                conditionBuilder.searchCondition(params))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsByCode(String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(externalSource)
                        .where(externalSource.code.eq(code))
                        .fetchFirst();
        return result != null;
    }
}
