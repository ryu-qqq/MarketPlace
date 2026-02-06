package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerSettlementJpaEntity.sellerSettlementJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerSettlementConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerSettlementQueryDslRepository - 셀러 정산 정보 QueryDSL 레포지토리.
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerSettlementQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerSettlementConditionBuilder conditionBuilder;

    public SellerSettlementQueryDslRepository(
            JPAQueryFactory queryFactory, SellerSettlementConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 정산 정보 조회.
     *
     * @param id 정산 정보 ID
     * @return 정산 정보 Optional
     */
    public Optional<SellerSettlementJpaEntity> findById(Long id) {
        SellerSettlementJpaEntity entity =
                queryFactory
                        .selectFrom(sellerSettlementJpaEntity)
                        .where(conditionBuilder.idEq(id), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID로 정산 정보 조회.
     *
     * @param sellerId 셀러 ID
     * @return 정산 정보 Optional
     */
    public Optional<SellerSettlementJpaEntity> findBySellerId(Long sellerId) {
        SellerSettlementJpaEntity entity =
                queryFactory
                        .selectFrom(sellerSettlementJpaEntity)
                        .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID로 정산 정보 존재 여부 확인.
     *
     * @param sellerId 셀러 ID
     * @return 존재하면 true
     */
    public boolean existsBySellerId(Long sellerId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerSettlementJpaEntity)
                        .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    private BooleanExpression notDeleted() {
        return sellerSettlementJpaEntity.deletedAt.isNull();
    }
}
