package com.ryuqq.marketplace.adapter.out.persistence.cancel.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.condition.CancelConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.QCancelJpaEntity;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.query.CancelSortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Cancel QueryDSL Repository.
 *
 * <p>복잡한 동적 쿼리를 처리합니다.
 */
@Repository
public class CancelQueryDslRepository {

    private static final QCancelJpaEntity cancel = QCancelJpaEntity.cancelJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final CancelConditionBuilder conditionBuilder;

    public CancelQueryDslRepository(
            JPAQueryFactory queryFactory, CancelConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 취소를 조회합니다.
     *
     * @param id 취소 ID
     * @return 취소 엔티티 (없으면 empty)
     */
    public Optional<CancelJpaEntity> findById(String id) {
        CancelJpaEntity entity =
                queryFactory.selectFrom(cancel).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 주문 ID로 취소를 조회합니다.
     *
     * @param orderId 주문 ID
     * @return 취소 엔티티 (없으면 empty)
     */
    public Optional<CancelJpaEntity> findByOrderId(String orderId) {
        CancelJpaEntity entity =
                queryFactory
                        .selectFrom(cancel)
                        .where(conditionBuilder.orderIdEq(orderId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 주문 ID 목록으로 취소 목록을 조회합니다.
     *
     * @param orderIds 주문 ID 목록
     * @return 취소 엔티티 목록
     */
    public List<CancelJpaEntity> findByOrderIds(List<String> orderIds) {
        return queryFactory.selectFrom(cancel).where(conditionBuilder.orderIdIn(orderIds)).fetch();
    }

    /**
     * 검색 조건으로 취소 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 취소 엔티티 목록
     */
    public List<CancelJpaEntity> findByCriteria(CancelSearchCriteria criteria) {
        return queryFactory
                .selectFrom(cancel)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.typeIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.dateRange(criteria))
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건에 해당하는 취소 건수를 반환합니다.
     *
     * @param criteria 검색 조건
     * @return 건수
     */
    public long countByCriteria(CancelSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(cancel.count())
                        .from(cancel)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.typeIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.dateRange(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(CancelSearchCriteria criteria) {
        CancelSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? cancel.createdAt.asc() : cancel.createdAt.desc();
            case REQUESTED_AT -> isAsc ? cancel.requestedAt.asc() : cancel.requestedAt.desc();
            case COMPLETED_AT -> isAsc ? cancel.completedAt.asc() : cancel.completedAt.desc();
        };
    }
}
