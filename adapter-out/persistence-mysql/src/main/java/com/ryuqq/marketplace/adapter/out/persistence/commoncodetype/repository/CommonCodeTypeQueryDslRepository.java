package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.QCommonCodeTypeJpaEntity.commonCodeTypeJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.condition.CommonCodeTypeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * CommonCodeTypeQueryDslRepository - 공통 코드 타입 QueryDSL 레포지토리.
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class CommonCodeTypeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final CommonCodeTypeConditionBuilder conditionBuilder;

    /**
     * 생성자 주입.
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public CommonCodeTypeQueryDslRepository(
            JPAQueryFactory queryFactory, CommonCodeTypeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 공통 코드 타입 조회.
     *
     * @param id 공통 코드 타입 ID
     * @return 공통 코드 타입 Optional
     */
    public Optional<CommonCodeTypeJpaEntity> findById(Long id) {
        CommonCodeTypeJpaEntity entity =
                queryFactory
                        .selectFrom(commonCodeTypeJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID 목록으로 공통 코드 타입 목록 조회.
     *
     * @param ids ID 목록
     * @return 공통 코드 타입 엔티티 목록
     */
    public List<CommonCodeTypeJpaEntity> findByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(commonCodeTypeJpaEntity)
                .where(conditionBuilder.idIn(ids), conditionBuilder.notDeleted())
                .fetch();
    }

    /**
     * 코드 존재 여부 확인.
     *
     * @param code 코드
     * @return 존재하면 true
     */
    public boolean existsByCode(String code) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(commonCodeTypeJpaEntity)
                        .where(conditionBuilder.codeEq(code), conditionBuilder.notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 표시 순서 존재 여부 확인 (특정 ID 제외).
     *
     * @param displayOrder 표시 순서
     * @param excludeId 제외할 ID
     * @return 존재하면 true
     */
    public boolean existsByDisplayOrderExcludingId(int displayOrder, Long excludeId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(commonCodeTypeJpaEntity)
                        .where(
                                conditionBuilder.displayOrderEq(displayOrder),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 검색 조건으로 공통 코드 타입 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 타입 엔티티 목록
     */
    public List<CommonCodeTypeJpaEntity> findByCriteria(CommonCodeTypeSearchCriteria criteria) {
        var qc = criteria.queryContext();
        return queryFactory
                .selectFrom(commonCodeTypeJpaEntity)
                .where(
                        deletedAtFilter(criteria),
                        conditionBuilder.activeEq(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.typeHasCommonCodeValue(criteria))
                .orderBy(createOrderSpecifier(qc.sortKey(), qc.sortDirection()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건으로 공통 코드 타입 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 타입 개수
     */
    public long countByCriteria(CommonCodeTypeSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(commonCodeTypeJpaEntity.count())
                        .from(commonCodeTypeJpaEntity)
                        .where(
                                deletedAtFilter(criteria),
                                conditionBuilder.activeEq(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.typeHasCommonCodeValue(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private BooleanExpression deletedAtFilter(CommonCodeTypeSearchCriteria criteria) {
        return criteria.queryContext().includeDeleted()
                ? null
                : commonCodeTypeJpaEntity.deletedAt.isNull();
    }

    private OrderSpecifier<?> createOrderSpecifier(
            CommonCodeTypeSortKey sortKey, SortDirection direction) {
        boolean isAsc = direction.isAscending();
        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc
                            ? commonCodeTypeJpaEntity.createdAt.asc()
                            : commonCodeTypeJpaEntity.createdAt.desc();
            case DISPLAY_ORDER ->
                    isAsc
                            ? commonCodeTypeJpaEntity.displayOrder.asc()
                            : commonCodeTypeJpaEntity.displayOrder.desc();
            case CODE ->
                    isAsc
                            ? commonCodeTypeJpaEntity.code.asc()
                            : commonCodeTypeJpaEntity.code.desc();
        };
    }
}
