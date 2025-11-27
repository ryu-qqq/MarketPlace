package com.ryuqq.marketplace.adapter.out.persistence.brand.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandAliasJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.QBrandJpaEntity;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchQuery;
import com.ryuqq.marketplace.application.brand.port.out.query.AliasMatchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Brand QueryDSL Repository
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java class</li>
 *   <li>QueryDSL DTO Projection 사용</li>
 *   <li>동적 쿼리 생성</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Repository
public class BrandQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;
    private final QBrandAliasJpaEntity alias = QBrandAliasJpaEntity.brandAliasJpaEntity;

    public BrandQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 브랜드 검색
     *
     * @param query 검색 조건
     * @param pageable 페이징 정보
     * @return Page<BrandJpaEntity>
     */
    public Page<BrandJpaEntity> search(BrandSearchQuery query, Pageable pageable) {
        List<BrandJpaEntity> content = queryFactory
            .selectFrom(brand)
            .where(
                keywordContains(query.keyword()),
                statusEq(query.status()),
                isLuxuryEq(query.isLuxury()),
                departmentEq(query.department()),
                countryEq(query.country())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(brand.count())
            .from(brand)
            .where(
                keywordContains(query.keyword()),
                statusEq(query.status()),
                isLuxuryEq(query.isLuxury()),
                departmentEq(query.department()),
                countryEq(query.country())
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 정규화된 별칭으로 브랜드 매칭 결과 조회
     *
     * @param normalizedAlias 정규화된 별칭
     * @return List<AliasMatchResult>
     */
    public List<AliasMatchResult> findByNormalizedAlias(String normalizedAlias) {
        return queryFactory
            .select(Projections.constructor(
                AliasMatchResult.class,
                brand.id,
                brand.code,
                brand.canonicalName,
                brand.nameKo,
                alias.confidence
            ))
            .from(alias)
            .join(brand).on(alias.brandId.eq(brand.id))
            .where(alias.normalizedAlias.eq(normalizedAlias))
            .fetch();
    }

    // ===== Dynamic Query Helpers =====

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return brand.nameKo.contains(keyword)
            .or(brand.nameEn.contains(keyword))
            .or(brand.code.contains(keyword));
    }

    private BooleanExpression statusEq(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return brand.status.stringValue().eq(status);
    }

    private BooleanExpression isLuxuryEq(Boolean isLuxury) {
        if (isLuxury == null) {
            return null;
        }
        return brand.isLuxury.eq(isLuxury);
    }

    private BooleanExpression departmentEq(String department) {
        if (department == null || department.isBlank()) {
            return null;
        }
        return brand.department.stringValue().eq(department);
    }

    private BooleanExpression countryEq(String country) {
        if (country == null || country.isBlank()) {
            return null;
        }
        return brand.country.eq(country);
    }
}
