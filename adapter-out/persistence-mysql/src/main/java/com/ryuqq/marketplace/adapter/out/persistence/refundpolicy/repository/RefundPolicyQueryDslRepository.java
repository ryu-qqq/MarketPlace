package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.QRefundPolicyJpaEntity.refundPolicyJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.condition.RefundPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * RefundPolicyQueryDslRepository - 환불 정책 QueryDSL 레포지토리.
 *
 * <p>PER-REP-002: 모든 조회 로직은 QueryDslRepository에서 처리.
 *
 * <p>PER-REP-004: ConditionBuilder를 사용하여 동적 쿼리 구성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class RefundPolicyQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final RefundPolicyConditionBuilder conditionBuilder;

    public RefundPolicyQueryDslRepository(
            JPAQueryFactory queryFactory, RefundPolicyConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 환불 정책 조회.
     *
     * @param id 환불 정책 ID
     * @return 환불 정책 Optional
     */
    public Optional<RefundPolicyJpaEntity> findById(Long id) {
        RefundPolicyJpaEntity entity =
                queryFactory
                        .selectFrom(refundPolicyJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID 목록으로 환불 정책 목록 조회.
     *
     * @param ids 환불 정책 ID 목록
     * @return 환불 정책 목록
     */
    public List<RefundPolicyJpaEntity> findByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(refundPolicyJpaEntity)
                .where(conditionBuilder.idIn(ids), conditionBuilder.notDeleted())
                .fetch();
    }

    /**
     * 셀러의 기본 정책 조회.
     *
     * @param sellerId 셀러 ID
     * @return 기본 환불 정책 Optional
     */
    public Optional<RefundPolicyJpaEntity> findDefaultBySellerId(Long sellerId) {
        RefundPolicyJpaEntity entity =
                queryFactory
                        .selectFrom(refundPolicyJpaEntity)
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
     * @return 환불 정책 Optional
     */
    public Optional<RefundPolicyJpaEntity> findBySellerIdAndId(Long sellerId, Long policyId) {
        RefundPolicyJpaEntity entity =
                queryFactory
                        .selectFrom(refundPolicyJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.idEq(policyId),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 검색 조건으로 환불 정책 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 환불 정책 목록
     */
    public List<RefundPolicyJpaEntity> findByCriteria(RefundPolicySearchCriteria criteria) {
        QueryContext<RefundPolicySortKey> qc = criteria.queryContext();

        return queryFactory
                .selectFrom(refundPolicyJpaEntity)
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
     * 검색 조건으로 환불 정책 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 환불 정책 개수
     */
    public long countByCriteria(RefundPolicySearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(refundPolicyJpaEntity.count())
                        .from(refundPolicyJpaEntity)
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
                        .select(refundPolicyJpaEntity.count())
                        .from(refundPolicyJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.activeEq(true),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private OrderSpecifier<?> createOrderSpecifier(
            RefundPolicySortKey sortKey, SortDirection direction) {
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc
                            ? refundPolicyJpaEntity.createdAt.asc()
                            : refundPolicyJpaEntity.createdAt.desc();
            case POLICY_NAME ->
                    isAsc
                            ? refundPolicyJpaEntity.policyName.asc()
                            : refundPolicyJpaEntity.policyName.desc();
            case RETURN_PERIOD_DAYS ->
                    isAsc
                            ? refundPolicyJpaEntity.returnPeriodDays.asc()
                            : refundPolicyJpaEntity.returnPeriodDays.desc();
        };
    }
}
