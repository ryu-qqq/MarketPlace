package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.condition.CanonicalOptionGroupConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.QCanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** CanonicalOptionGroup QueryDSL Repository. */
@Repository
public class CanonicalOptionGroupQueryDslRepository {

    private static final QCanonicalOptionGroupJpaEntity canonicalOptionGroup =
            QCanonicalOptionGroupJpaEntity.canonicalOptionGroupJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final CanonicalOptionGroupConditionBuilder conditionBuilder;

    public CanonicalOptionGroupQueryDslRepository(
            JPAQueryFactory queryFactory, CanonicalOptionGroupConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<CanonicalOptionGroupJpaEntity> findById(Long id) {
        CanonicalOptionGroupJpaEntity entity =
                queryFactory
                        .selectFrom(canonicalOptionGroup)
                        .where(conditionBuilder.idEq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<CanonicalOptionGroupJpaEntity> findByCriteria(
            CanonicalOptionGroupSearchCriteria criteria) {
        return queryFactory
                .selectFrom(canonicalOptionGroup)
                .where(
                        conditionBuilder.activeEq(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(canonicalOptionGroup.count())
                        .from(canonicalOptionGroup)
                        .where(
                                conditionBuilder.activeEq(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(CanonicalOptionGroupSearchCriteria criteria) {
        CanonicalOptionGroupSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc
                            ? canonicalOptionGroup.createdAt.asc()
                            : canonicalOptionGroup.createdAt.desc();
            case CODE -> isAsc ? canonicalOptionGroup.code.asc() : canonicalOptionGroup.code.desc();
        };
    }
}
