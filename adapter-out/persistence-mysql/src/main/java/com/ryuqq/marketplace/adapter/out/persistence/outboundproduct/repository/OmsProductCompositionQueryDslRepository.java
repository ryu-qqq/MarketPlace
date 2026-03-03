package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity.brandJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.QOutboundProductJpaEntity.outboundProductJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QProductGroupJpaEntity.productGroupJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerJpaEntity.sellerJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductListCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition.OmsProductConditionBuilder;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * OmsProductCompositionQueryDslRepository - OMS 상품 목록 Composition QueryDSL 레포지토리.
 *
 * <p>product_groups WHERE EXISTS(outbound_products) LEFT JOIN sellers LEFT JOIN brands + 필터 + 페이징.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class OmsProductCompositionQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OmsProductConditionBuilder conditionBuilder;

    public OmsProductCompositionQueryDslRepository(
            JPAQueryFactory queryFactory, OmsProductConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 검색 조건에 맞는 OMS 상품 목록 조회.
     *
     * @param criteria 검색 조건
     * @return OMS 상품 Composite DTO 목록
     */
    public List<OmsProductListCompositeDto> findByCriteria(OmsProductSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OmsProductListCompositeDto.class,
                                productGroupJpaEntity.id,
                                productGroupJpaEntity.productGroupName,
                                productGroupJpaEntity.status,
                                sellerJpaEntity.id,
                                sellerJpaEntity.sellerName,
                                brandJpaEntity.id,
                                brandJpaEntity.nameKo,
                                productGroupJpaEntity.createdAt,
                                productGroupJpaEntity.updatedAt))
                .from(productGroupJpaEntity)
                .leftJoin(sellerJpaEntity)
                .on(sellerJpaEntity.id.eq(productGroupJpaEntity.sellerId))
                .leftJoin(brandJpaEntity)
                .on(brandJpaEntity.id.eq(productGroupJpaEntity.brandId))
                .where(
                        outboundProductExists(),
                        conditionBuilder.notDeleted(),
                        buildStatusCondition(criteria),
                        buildPartnerCondition(criteria),
                        buildSearchCondition(criteria),
                        buildDateCondition(criteria))
                .orderBy(productGroupJpaEntity.createdAt.desc())
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * 검색 조건에 맞는 전체 건수 조회.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    public long countByCriteria(OmsProductSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(productGroupJpaEntity.count())
                        .from(productGroupJpaEntity)
                        .leftJoin(sellerJpaEntity)
                        .on(sellerJpaEntity.id.eq(productGroupJpaEntity.sellerId))
                        .where(
                                outboundProductExists(),
                                conditionBuilder.notDeleted(),
                                buildStatusCondition(criteria),
                                buildPartnerCondition(criteria),
                                buildSearchCondition(criteria),
                                buildDateCondition(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    // -- private helpers --

    private BooleanExpression outboundProductExists() {
        return JPAExpressions.selectOne()
                .from(outboundProductJpaEntity)
                .where(outboundProductJpaEntity.productGroupId.eq(productGroupJpaEntity.id))
                .exists();
    }

    private BooleanExpression buildStatusCondition(OmsProductSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusStrings = criteria.statuses().stream().map(Enum::name).toList();
        return conditionBuilder.statusIn(statusStrings);
    }

    private BooleanExpression buildPartnerCondition(OmsProductSearchCriteria criteria) {
        if (!criteria.hasPartnerFilter()) {
            return null;
        }
        return conditionBuilder.sellerIdIn(criteria.partnerIds());
    }

    private BooleanExpression buildSearchCondition(OmsProductSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        return conditionBuilder.searchFieldContains(criteria.searchField(), criteria.searchWord());
    }

    private BooleanExpression buildDateCondition(OmsProductSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }
        Instant start = criteria.dateRange().startInstant();
        Instant end = criteria.dateRange().endInstant();
        if ("UPDATED_AT".equalsIgnoreCase(criteria.dateType())) {
            return conditionBuilder.updatedAtBetween(start, end);
        }
        return conditionBuilder.createdAtBetween(start, end);
    }
}
