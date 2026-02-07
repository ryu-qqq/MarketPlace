package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerCsJpaEntity.sellerCsJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerCsConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerCsQueryDslRepository - 셀러 CS 정보 QueryDSL 레포지토리.
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerCsQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerCsConditionBuilder conditionBuilder;

    public SellerCsQueryDslRepository(
            JPAQueryFactory queryFactory, SellerCsConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 CS 정보 조회.
     *
     * @param id CS ID
     * @return CS 정보 Optional
     */
    public Optional<SellerCsJpaEntity> findById(Long id) {
        SellerCsJpaEntity entity =
                queryFactory
                        .selectFrom(sellerCsJpaEntity)
                        .where(conditionBuilder.idEq(id), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID로 CS 정보 조회.
     *
     * @param sellerId 셀러 ID
     * @return CS 정보 Optional
     */
    public Optional<SellerCsJpaEntity> findBySellerId(Long sellerId) {
        SellerCsJpaEntity entity =
                queryFactory
                        .selectFrom(sellerCsJpaEntity)
                        .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID로 CS 정보 존재 여부 확인.
     *
     * @param sellerId 셀러 ID
     * @return 존재하면 true
     */
    public boolean existsBySellerId(Long sellerId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerCsJpaEntity)
                        .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    private BooleanExpression notDeleted() {
        return sellerCsJpaEntity.deletedAt.isNull();
    }
}
