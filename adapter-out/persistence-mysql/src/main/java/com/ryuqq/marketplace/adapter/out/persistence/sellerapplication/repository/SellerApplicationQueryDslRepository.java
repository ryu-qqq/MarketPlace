package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.QSellerApplicationJpaEntity.sellerApplicationJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.condition.SellerApplicationConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSortKey;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerApplicationQueryDslRepository - 입점 신청 QueryDSL 레포지토리.
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerApplicationQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerApplicationConditionBuilder conditionBuilder;

    public SellerApplicationQueryDslRepository(
            JPAQueryFactory queryFactory, SellerApplicationConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 입점 신청 조회.
     *
     * @param id 신청 ID
     * @return 입점 신청 Optional
     */
    public Optional<SellerApplicationJpaEntity> findById(Long id) {
        SellerApplicationJpaEntity entity =
                queryFactory
                        .selectFrom(sellerApplicationJpaEntity)
                        .where(conditionBuilder.idEq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID 존재 여부 확인.
     *
     * @param id 신청 ID
     * @return 존재하면 true
     */
    public boolean existsById(Long id) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerApplicationJpaEntity)
                        .where(conditionBuilder.idEq(id))
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 사업자등록번호로 대기 중인 신청 존재 여부 확인.
     *
     * @param registrationNumber 사업자등록번호
     * @return 존재하면 true
     */
    public boolean existsPendingByRegistrationNumber(String registrationNumber) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerApplicationJpaEntity)
                        .where(
                                conditionBuilder.registrationNumberEq(registrationNumber),
                                conditionBuilder.statusEq(ApplicationStatus.PENDING))
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 승인된 셀러 ID로 입점 신청 조회.
     *
     * @param approvedSellerId 승인된 셀러 ID
     * @return 입점 신청 Optional
     */
    public Optional<SellerApplicationJpaEntity> findByApprovedSellerId(Long approvedSellerId) {
        SellerApplicationJpaEntity entity =
                queryFactory
                        .selectFrom(sellerApplicationJpaEntity)
                        .where(conditionBuilder.approvedSellerIdEq(approvedSellerId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 검색 조건으로 입점 신청 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 입점 신청 엔티티 목록
     */
    public List<SellerApplicationJpaEntity> findByCriteria(
            SellerApplicationSearchCriteria criteria) {
        var qc = criteria.queryContext();
        return queryFactory
                .selectFrom(sellerApplicationJpaEntity)
                .where(
                        conditionBuilder.statusCondition(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(createOrderSpecifier(qc.sortKey(), qc.sortDirection()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건으로 입점 신청 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 개수
     */
    public long countByCriteria(SellerApplicationSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(sellerApplicationJpaEntity.count())
                        .from(sellerApplicationJpaEntity)
                        .where(
                                conditionBuilder.statusCondition(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private OrderSpecifier<?> createOrderSpecifier(
            SellerApplicationSortKey sortKey, SortDirection direction) {
        boolean isAsc = direction.isAscending();
        return switch (sortKey) {
            case APPLIED_AT ->
                    isAsc
                            ? sellerApplicationJpaEntity.appliedAt.asc()
                            : sellerApplicationJpaEntity.appliedAt.desc();
        };
    }
}
