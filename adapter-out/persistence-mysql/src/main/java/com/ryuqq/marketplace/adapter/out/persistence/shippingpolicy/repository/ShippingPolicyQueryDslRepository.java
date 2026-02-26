package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.QShippingPolicyJpaEntity.shippingPolicyJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.condition.ShippingPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ShippingPolicyQueryDslRepository - 배송 정책 QueryDSL 레포지토리.
 *
 * <p>PER-REP-002: 모든 조회 로직은 QueryDslRepository에서 처리.
 *
 * <p>PER-REP-004: ConditionBuilder를 사용하여 동적 쿼리 구성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class ShippingPolicyQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ShippingPolicyConditionBuilder conditionBuilder;

    public ShippingPolicyQueryDslRepository(
            JPAQueryFactory queryFactory, ShippingPolicyConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 배송 정책 조회.
     *
     * @param id 배송 정책 ID
     * @return 배송 정책 Optional
     */
    public Optional<ShippingPolicyJpaEntity> findById(Long id) {
        ShippingPolicyJpaEntity entity =
                queryFactory
                        .selectFrom(shippingPolicyJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID 목록으로 배송 정책 목록 조회.
     *
     * @param ids 배송 정책 ID 목록
     * @return 배송 정책 목록
     */
    public List<ShippingPolicyJpaEntity> findByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(shippingPolicyJpaEntity)
                .where(conditionBuilder.idIn(ids), conditionBuilder.notDeleted())
                .fetch();
    }

    /**
     * 셀러의 기본 정책 조회.
     *
     * @param sellerId 셀러 ID
     * @return 기본 배송 정책 Optional
     */
    public Optional<ShippingPolicyJpaEntity> findDefaultBySellerId(Long sellerId) {
        ShippingPolicyJpaEntity entity =
                queryFactory
                        .selectFrom(shippingPolicyJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.defaultPolicyEq(true),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID와 정책 ID로 조회.
     *
     * @param sellerId 셀러 ID
     * @param policyId 정책 ID
     * @return 배송 정책 Optional
     */
    public Optional<ShippingPolicyJpaEntity> findBySellerIdAndId(Long sellerId, Long policyId) {
        ShippingPolicyJpaEntity entity =
                queryFactory
                        .selectFrom(shippingPolicyJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.idEq(policyId),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 검색 조건으로 배송 정책 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 배송 정책 목록
     */
    public List<ShippingPolicyJpaEntity> findByCriteria(ShippingPolicySearchCriteria criteria) {
        QueryContext<ShippingPolicySortKey> qc = criteria.queryContext();

        return queryFactory
                .selectFrom(shippingPolicyJpaEntity)
                .where(
                        conditionBuilder.sellerIdEq(criteria.sellerIdValue()),
                        conditionBuilder.activeEq(criteria.active()),
                        conditionBuilder.notDeleted())
                .orderBy(createOrderSpecifier(qc.sortKey(), qc.sortDirection()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건으로 배송 정책 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 배송 정책 개수
     */
    public long countByCriteria(ShippingPolicySearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(shippingPolicyJpaEntity.count())
                        .from(shippingPolicyJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(criteria.sellerIdValue()),
                                conditionBuilder.activeEq(criteria.active()),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 셀러의 활성 정책 개수 조회.
     *
     * <p>POL-DEACT-002: 마지막 활성 정책 비활성화 검증에 사용
     *
     * @param sellerId 셀러 ID
     * @return 활성 정책 개수
     */
    public long countActiveBySellerId(Long sellerId) {
        Long count =
                queryFactory
                        .select(shippingPolicyJpaEntity.count())
                        .from(shippingPolicyJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.activeEq(true),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private OrderSpecifier<?> createOrderSpecifier(
            ShippingPolicySortKey sortKey, SortDirection direction) {
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc
                            ? shippingPolicyJpaEntity.createdAt.asc()
                            : shippingPolicyJpaEntity.createdAt.desc();
            case POLICY_NAME ->
                    isAsc
                            ? shippingPolicyJpaEntity.policyName.asc()
                            : shippingPolicyJpaEntity.policyName.desc();
            case BASE_FEE ->
                    isAsc
                            ? shippingPolicyJpaEntity.baseFee.asc()
                            : shippingPolicyJpaEntity.baseFee.desc();
        };
    }
}
