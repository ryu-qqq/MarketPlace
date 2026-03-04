package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.QSellerAdminJpaEntity.sellerAdminJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition.SellerAdminConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSortKey;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerAdminQueryDslRepository - 셀러 관리자 QueryDSL 레포지토리.
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerAdminQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerAdminConditionBuilder conditionBuilder;

    public SellerAdminQueryDslRepository(
            JPAQueryFactory queryFactory, SellerAdminConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 셀러 관리자 조회.
     *
     * @param id 셀러 관리자 ID (UUIDv7)
     * @return 셀러 관리자 Optional
     */
    public Optional<SellerAdminJpaEntity> findById(String id) {
        SellerAdminJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * AuthHub 사용자 ID로 셀러 관리자 조회.
     *
     * @param authUserId AuthHub 사용자 ID
     * @return 셀러 관리자 Optional
     */
    public Optional<SellerAdminJpaEntity> findByAuthUserId(String authUserId) {
        SellerAdminJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminJpaEntity)
                        .where(
                                conditionBuilder.authUserIdEq(authUserId),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID와 셀러 관리자 ID로 조회.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @return 셀러 관리자 Optional
     */
    public Optional<SellerAdminJpaEntity> findBySellerIdAndId(Long sellerId, String sellerAdminId) {
        SellerAdminJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.idEq(sellerAdminId),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 ID, 셀러 관리자 ID, 상태로 조회.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자 Optional
     */
    public Optional<SellerAdminJpaEntity> findBySellerIdAndIdAndStatuses(
            Long sellerId, String sellerAdminId, List<SellerAdminStatus> statuses) {
        SellerAdminJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminJpaEntity)
                        .where(
                                conditionBuilder.sellerIdEq(sellerId),
                                conditionBuilder.idEq(sellerAdminId),
                                conditionBuilder.statusIn(statuses),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 셀러 관리자 ID와 상태로 조회.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자 Optional
     */
    public Optional<SellerAdminJpaEntity> findByIdAndStatuses(
            String sellerAdminId, List<SellerAdminStatus> statuses) {
        SellerAdminJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminJpaEntity)
                        .where(
                                conditionBuilder.idEq(sellerAdminId),
                                conditionBuilder.statusIn(statuses),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID 목록으로 셀러 관리자 일괄 조회.
     *
     * @param ids 셀러 관리자 ID 목록 (UUIDv7)
     * @return 셀러 관리자 엔티티 목록
     */
    public List<SellerAdminJpaEntity> findAllByIds(List<String> ids) {
        return queryFactory
                .selectFrom(sellerAdminJpaEntity)
                .where(conditionBuilder.idsIn(ids), conditionBuilder.notDeleted())
                .fetch();
    }

    /**
     * 검색 조건으로 셀러 관리자 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 관리자 엔티티 목록
     */
    public List<SellerAdminJpaEntity> findByCriteria(SellerAdminSearchCriteria criteria) {
        var qc = criteria.queryContext();
        return queryFactory
                .selectFrom(sellerAdminJpaEntity)
                .where(
                        conditionBuilder.sellerIdsIn(criteria.sellerIds()),
                        conditionBuilder.statusIn(criteria.status()),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.dateRangeCondition(criteria),
                        conditionBuilder.notDeleted())
                .orderBy(createOrderSpecifier(qc.sortKey(), qc.sortDirection()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건으로 셀러 관리자 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 관리자 개수
     */
    public long countByCriteria(SellerAdminSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(sellerAdminJpaEntity.count())
                        .from(sellerAdminJpaEntity)
                        .where(
                                conditionBuilder.sellerIdsIn(criteria.sellerIds()),
                                conditionBuilder.statusIn(criteria.status()),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.dateRangeCondition(criteria),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 이름과 로그인 ID로 셀러 관리자 조회.
     *
     * @param name 관리자 이름
     * @param loginId 로그인 ID
     * @return 셀러 관리자 Optional
     */
    public Optional<SellerAdminJpaEntity> findByNameAndLoginId(String name, String loginId) {
        SellerAdminJpaEntity entity =
                queryFactory
                        .selectFrom(sellerAdminJpaEntity)
                        .where(
                                conditionBuilder.nameEq(name),
                                conditionBuilder.loginIdEq(loginId),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return Optional.ofNullable(entity);
    }

    /**
     * 로그인 ID 존재 여부 확인.
     *
     * @param loginId 로그인 ID
     * @return 존재하면 true
     */
    public boolean existsByLoginId(String loginId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerAdminJpaEntity)
                        .where(conditionBuilder.loginIdEq(loginId), conditionBuilder.notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    private OrderSpecifier<?> createOrderSpecifier(
            SellerAdminSortKey sortKey, SortDirection direction) {
        boolean isAsc = direction.isAscending();
        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc
                            ? sellerAdminJpaEntity.createdAt.asc()
                            : sellerAdminJpaEntity.createdAt.desc();
        };
    }
}
