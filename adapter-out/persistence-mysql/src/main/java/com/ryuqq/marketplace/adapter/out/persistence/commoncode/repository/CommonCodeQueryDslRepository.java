package com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.QCommonCodeJpaEntity.commonCodeJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.QCommonCodeTypeJpaEntity.commonCodeTypeJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.condition.CommonCodeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * CommonCodeQueryDslRepository - 공통 코드 QueryDSL 레포지토리.
 *
 * <p>PER-REP-002: 모든 조회 로직은 QueryDslRepository에서 처리.
 *
 * <p>PER-REP-004: ConditionBuilder를 사용하여 동적 쿼리 구성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class CommonCodeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final CommonCodeConditionBuilder conditionBuilder;

    public CommonCodeQueryDslRepository(
            JPAQueryFactory queryFactory, CommonCodeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 공통 코드 조회.
     *
     * @param id 공통 코드 ID
     * @return 공통 코드 Optional
     */
    public Optional<CommonCodeJpaEntity> findById(Long id) {
        CommonCodeJpaEntity entity =
                queryFactory
                        .selectFrom(commonCodeJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * ID 목록으로 공통 코드 목록 조회.
     *
     * @param ids 공통 코드 ID 목록
     * @return 공통 코드 목록
     */
    public List<CommonCodeJpaEntity> findByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(commonCodeJpaEntity)
                .where(conditionBuilder.idIn(ids), conditionBuilder.notDeleted())
                .fetch();
    }

    /**
     * 타입 ID + 코드 존재 여부 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @param code 코드값
     * @return 존재하면 true
     */
    public boolean existsByCommonCodeTypeIdAndCode(Long commonCodeTypeId, String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(commonCodeJpaEntity)
                        .where(
                                conditionBuilder.commonCodeTypeIdEq(commonCodeTypeId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 특정 타입 ID의 활성화된 공통 코드 존재 여부 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @return 활성화된 공통 코드 존재 여부
     */
    public boolean existsActiveByCommonCodeTypeId(Long commonCodeTypeId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(commonCodeJpaEntity)
                        .where(
                                conditionBuilder.commonCodeTypeIdEq(commonCodeTypeId),
                                conditionBuilder.activeEq(true),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 검색 조건으로 공통 코드 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 목록
     */
    public List<CommonCodeJpaEntity> findByCriteria(CommonCodeSearchCriteria criteria) {
        QueryContext<CommonCodeSortKey> qc = criteria.queryContext();

        JPAQuery<CommonCodeJpaEntity> query = queryFactory.selectFrom(commonCodeJpaEntity);

        applyCommonCodeTypeJoin(query, criteria.commonCodeTypeCode());

        return query.where(
                        conditionBuilder.commonCodeTypeCodeEq(criteria.commonCodeTypeCode()),
                        conditionBuilder.activeEq(criteria.active()),
                        conditionBuilder.notDeleted())
                .orderBy(createOrderSpecifier(qc.sortKey(), qc.sortDirection()))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건으로 공통 코드 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 개수
     */
    public long countByCriteria(CommonCodeSearchCriteria criteria) {
        JPAQuery<Long> query =
                queryFactory.select(commonCodeJpaEntity.count()).from(commonCodeJpaEntity);

        applyCommonCodeTypeJoin(query, criteria.commonCodeTypeCode());

        Long count =
                query.where(
                                conditionBuilder.commonCodeTypeCodeEq(
                                        criteria.commonCodeTypeCode()),
                                conditionBuilder.activeEq(criteria.active()),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    private void applyCommonCodeTypeJoin(JPAQuery<?> query, String commonCodeTypeCode) {
        if (commonCodeTypeCode != null && !commonCodeTypeCode.isBlank()) {
            query.join(commonCodeTypeJpaEntity)
                    .on(
                            commonCodeJpaEntity
                                    .commonCodeTypeId
                                    .eq(commonCodeTypeJpaEntity.id)
                                    .and(conditionBuilder.commonCodeTypeNotDeleted()));
        }
    }

    private OrderSpecifier<?> createOrderSpecifier(
            CommonCodeSortKey sortKey, SortDirection direction) {
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT ->
                    isAsc
                            ? commonCodeJpaEntity.createdAt.asc()
                            : commonCodeJpaEntity.createdAt.desc();
            case DISPLAY_ORDER ->
                    isAsc
                            ? commonCodeJpaEntity.displayOrder.asc()
                            : commonCodeJpaEntity.displayOrder.desc();
            case CODE -> isAsc ? commonCodeJpaEntity.code.asc() : commonCodeJpaEntity.code.desc();
        };
    }
}
