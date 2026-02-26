package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerJpaEntity.sellerJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import com.ryuqq.marketplace.domain.seller.query.SellerSortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * SellerQueryDslRepository - 셀러 QueryDSL 레포지토리.
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerConditionBuilder conditionBuilder;

    public SellerQueryDslRepository(
            JPAQueryFactory queryFactory, SellerConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 셀러 조회.
     *
     * @param id 셀러 ID
     * @return 셀러 Optional
     */
    public Optional<SellerJpaEntity> findById(Long id) {
        SellerJpaEntity entity =
                queryFactory
                        .selectFrom(sellerJpaEntity)
                        .where(conditionBuilder.idEq(id), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID 목록으로 셀러 목록 조회.
     *
     * @param ids ID 목록
     * @return 셀러 엔티티 목록
     */
    public List<SellerJpaEntity> findByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(sellerJpaEntity)
                .where(conditionBuilder.idIn(ids), notDeleted())
                .fetch();
    }

    /**
     * ID 존재 여부 확인.
     *
     * @param id 셀러 ID
     * @return 존재하면 true
     */
    public boolean existsById(Long id) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerJpaEntity)
                        .where(conditionBuilder.idEq(id), notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 셀러명 존재 여부 확인.
     *
     * @param sellerName 셀러명
     * @return 존재하면 true
     */
    public boolean existsBySellerName(String sellerName) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerJpaEntity)
                        .where(conditionBuilder.sellerNameEq(sellerName), notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 셀러명 존재 여부 확인 (특정 ID 제외).
     *
     * @param sellerName 셀러명
     * @param excludeId 제외할 셀러 ID
     * @return 존재하면 true
     */
    public boolean existsBySellerNameExcluding(String sellerName, Long excludeId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(sellerJpaEntity)
                        .where(
                                conditionBuilder.sellerNameEq(sellerName),
                                conditionBuilder.idNe(excludeId),
                                notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 검색 조건으로 셀러 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 엔티티 목록
     */
    public List<SellerJpaEntity> findByCriteria(SellerSearchCriteria criteria) {
        var qc = criteria.queryContext();
        return queryFactory
                .selectFrom(sellerJpaEntity)
                .where(
                        deletedAtFilter(criteria),
                        conditionBuilder.activeEq(criteria),
                        conditionBuilder.searchCondition(criteria))
                .orderBy(createOrderSpecifier(qc.sortKey(), qc.sortDirection()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건으로 셀러 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 개수
     */
    public long countByCriteria(SellerSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(sellerJpaEntity.count())
                        .from(sellerJpaEntity)
                        .where(
                                deletedAtFilter(criteria),
                                conditionBuilder.activeEq(criteria),
                                conditionBuilder.searchCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * authOrganizationId로 셀러 ID 조회.
     *
     * @param organizationId 인증 조직 ID
     * @return 셀러 ID Optional
     */
    public Optional<Long> findIdByOrganizationId(String organizationId) {
        Long id =
                queryFactory
                        .select(sellerJpaEntity.id)
                        .from(sellerJpaEntity)
                        .where(conditionBuilder.organizationIdEq(organizationId), notDeleted())
                        .fetchOne();
        return Optional.ofNullable(id);
    }

    private BooleanExpression notDeleted() {
        return sellerJpaEntity.deletedAt.isNull();
    }

    private BooleanExpression deletedAtFilter(SellerSearchCriteria criteria) {
        return criteria.queryContext().includeDeleted() ? null : notDeleted();
    }

    private OrderSpecifier<?> createOrderSpecifier(SellerSortKey sortKey, SortDirection direction) {
        boolean isAsc = direction.isAscending();
        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc ? sellerJpaEntity.createdAt.asc() : sellerJpaEntity.createdAt.desc();
            case SELLER_NAME ->
                    isAsc ? sellerJpaEntity.sellerName.asc() : sellerJpaEntity.sellerName.desc();
            case DISPLAY_NAME ->
                    isAsc ? sellerJpaEntity.displayName.asc() : sellerJpaEntity.displayName.desc();
        };
    }
}
