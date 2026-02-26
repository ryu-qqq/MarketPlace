package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerBusinessInfoJpaEntity.sellerBusinessInfoJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerBusinessInfoConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerBusinessInfoQueryDslRepository - 셀러 사업자 정보 QueryDSL 레포지토리.
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerBusinessInfoQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerBusinessInfoConditionBuilder conditionBuilder;

    public SellerBusinessInfoQueryDslRepository(
            JPAQueryFactory queryFactory, SellerBusinessInfoConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 사업자 정보 조회.
     *
     * @param id 사업자 정보 ID
     * @return 사업자 정보 Optional
     */
    public Optional<SellerBusinessInfoJpaEntity> findById(Long id) {
        SellerBusinessInfoJpaEntity entity =
                queryFactory
                        .selectFrom(sellerBusinessInfoJpaEntity)
                        .where(conditionBuilder.idEq(id), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID로 사업자 정보 조회.
     *
     * @param sellerId 셀러 ID
     * @return 사업자 정보 Optional
     */
    public Optional<SellerBusinessInfoJpaEntity> findBySellerId(Long sellerId) {
        SellerBusinessInfoJpaEntity entity =
                queryFactory
                        .selectFrom(sellerBusinessInfoJpaEntity)
                        .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID 존재 여부 확인.
     *
     * @param sellerId 셀러 ID
     * @return 존재하면 true
     */
    public boolean existsBySellerId(Long sellerId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerBusinessInfoJpaEntity)
                        .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 사업자등록번호 존재 여부 확인.
     *
     * @param registrationNumber 사업자등록번호
     * @return 존재하면 true
     */
    public boolean existsByRegistrationNumber(String registrationNumber) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerBusinessInfoJpaEntity)
                        .where(
                                conditionBuilder.registrationNumberEq(registrationNumber),
                                notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 사업자등록번호 존재 여부 확인 (특정 셀러 ID 제외).
     *
     * @param registrationNumber 사업자등록번호
     * @param excludeSellerId 제외할 셀러 ID
     * @return 존재하면 true
     */
    public boolean existsByRegistrationNumberExcluding(
            String registrationNumber, Long excludeSellerId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerBusinessInfoJpaEntity)
                        .where(
                                conditionBuilder.registrationNumberEq(registrationNumber),
                                conditionBuilder.sellerIdNe(excludeSellerId),
                                notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    private BooleanExpression notDeleted() {
        return sellerBusinessInfoJpaEntity.deletedAt.isNull();
    }
}
