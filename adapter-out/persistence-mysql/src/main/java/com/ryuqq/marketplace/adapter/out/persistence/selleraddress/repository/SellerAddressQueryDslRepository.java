package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.QSellerAddressJpaEntity.sellerAddressJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.condition.SellerAddressConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerAddressQueryDslRepository - 셀러 주소 QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerAddressQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerAddressConditionBuilder conditionBuilder;

    public SellerAddressQueryDslRepository(
            JPAQueryFactory queryFactory, SellerAddressConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<SellerAddressJpaEntity> findById(Long id) {
        SellerAddressJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAddressJpaEntity)
                        .where(conditionBuilder.idEq(id), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<SellerAddressJpaEntity> findAllBySellerId(Long sellerId) {
        return queryFactory
                .selectFrom(sellerAddressJpaEntity)
                .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                .orderBy(sellerAddressJpaEntity.createdAt.desc())
                .fetch();
    }

    public Optional<SellerAddressJpaEntity> findDefaultAddress(Long sellerId, String addressType) {
        SellerAddressJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAddressJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.addressTypeEq(addressType),
                                conditionBuilder.defaultAddressEq(true),
                                notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public boolean existsBySellerId(Long sellerId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerAddressJpaEntity)
                        .where(conditionBuilder.sellerIdEq(sellerId), notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    public boolean existsBySellerIdAndAddressTypeAndAddressName(
            Long sellerId, String addressType, String addressName) {
        if (addressName == null || addressName.isBlank()) {
            return false;
        }
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerAddressJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.addressTypeEq(addressType),
                                conditionBuilder.addressNameEq(addressName),
                                notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    public List<SellerAddressJpaEntity> search(BooleanBuilder conditions, long offset, int size) {
        return queryFactory
                .selectFrom(sellerAddressJpaEntity)
                .where(conditions, notDeleted())
                .orderBy(sellerAddressJpaEntity.createdAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    public long count(BooleanBuilder conditions) {
        Long count =
                queryFactory
                        .select(sellerAddressJpaEntity.count())
                        .from(sellerAddressJpaEntity)
                        .where(conditions, notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private BooleanExpression notDeleted() {
        return sellerAddressJpaEntity.deletedAt.isNull();
    }
}
