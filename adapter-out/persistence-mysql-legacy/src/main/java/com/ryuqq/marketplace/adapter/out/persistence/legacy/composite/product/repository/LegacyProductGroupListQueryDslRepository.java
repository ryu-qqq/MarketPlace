package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.brand.entity.QLegacyBrandEntity.legacyBrandEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.category.entity.QLegacyCategoryEntity.legacyCategoryEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.QLegacyOptionDetailEntity.legacyOptionDetailEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.QLegacyOptionGroupEntity.legacyOptionGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductEntity.legacyProductEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductOptionEntity.legacyProductOptionEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductStockEntity.legacyProductStockEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.entity.QLegacyProductDeliveryEntity.legacyProductDeliveryEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.QLegacyProductGroupEntity.legacyProductGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.QLegacyProductGroupImageEntity.legacyProductGroupImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.seller.entity.QLegacySellerEntity.legacySellerEntity;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupListQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 상품그룹 목록 조회 QueryDSL Repository.
 *
 * <p>3-Phase Query 패턴 적용:
 *
 * <ul>
 *   <li>Phase 1: WHERE 조건 + offset/limit으로 상품그룹 ID 목록만 조회
 *   <li>Phase 2: ID IN으로 상품그룹 상세 정보 조회 (multi-table JOIN)
 *   <li>Phase 3: ID IN으로 상품+옵션+재고 조회
 * </ul>
 *
 * <p>JOIN으로 인한 페이징 오류를 방지하기 위해 Phase 1에서 ID만 먼저 추출합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Repository
public class LegacyProductGroupListQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final LegacyProductGroupListConditionBuilder conditionBuilder;

    public LegacyProductGroupListQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory,
            LegacyProductGroupListConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * Phase 1: 검색 조건에 맞는 상품그룹 ID 목록을 조회합니다 (Offset 기반).
     *
     * @param criteria 검색 조건
     * @return 상품그룹 ID 목록
     */
    public List<Long> fetchProductGroupIds(LegacyProductGroupSearchCriteria criteria) {
        return queryFactory
                .select(legacyProductGroupEntity.id)
                .from(legacyProductGroupEntity)
                .where(conditionBuilder.buildConditions(criteria))
                .orderBy(legacyProductGroupEntity.id.desc())
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * Phase 2: 상품그룹 ID 목록으로 상세 정보를 조회합니다.
     *
     * <p>product_group + seller + brand + category + product_delivery + product_group_image(MAIN)
     * JOIN
     *
     * @param productGroupIds 상품그룹 ID 목록
     * @return flat projection DTO 목록
     */
    public List<LegacyProductGroupListQueryDto> fetchProductGroupDetails(
            List<Long> productGroupIds) {
        if (productGroupIds == null || productGroupIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .select(createListProjection())
                .from(legacyProductGroupEntity)
                .innerJoin(legacySellerEntity)
                .on(legacySellerEntity.id.eq(legacyProductGroupEntity.sellerId))
                .innerJoin(legacyBrandEntity)
                .on(legacyBrandEntity.id.eq(legacyProductGroupEntity.brandId))
                .leftJoin(legacyCategoryEntity)
                .on(legacyCategoryEntity.id.eq(legacyProductGroupEntity.categoryId))
                .leftJoin(legacyProductDeliveryEntity)
                .on(legacyProductDeliveryEntity.productGroupId.eq(legacyProductGroupEntity.id))
                .leftJoin(legacyProductGroupImageEntity)
                .on(
                        legacyProductGroupImageEntity.productGroupId.eq(
                                legacyProductGroupEntity.id),
                        legacyProductGroupImageEntity.deleteYn.eq("N"),
                        legacyProductGroupImageEntity.productGroupImageType.eq("MAIN"))
                .where(legacyProductGroupEntity.id.in(productGroupIds))
                .orderBy(legacyProductGroupEntity.id.desc())
                .fetch();
    }

    /**
     * Phase 3: 상품그룹 ID 목록으로 상품+옵션+재고 데이터를 조회합니다.
     *
     * <p>product + product_stock + product_option + option_group + option_detail JOIN
     *
     * @param productGroupIds 상품그룹 ID 목록
     * @return flat projection DTO 목록
     */
    public List<LegacyProductOptionQueryDto> fetchProductsWithOptions(List<Long> productGroupIds) {
        if (productGroupIds == null || productGroupIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .select(
                        Projections.constructor(
                                LegacyProductOptionQueryDto.class,
                                legacyProductEntity.id,
                                legacyProductEntity.productGroupId,
                                legacyProductEntity.soldOutYn,
                                legacyProductStockEntity.stockQuantity,
                                legacyOptionGroupEntity.id,
                                legacyOptionDetailEntity.id,
                                legacyOptionGroupEntity.optionName,
                                legacyOptionDetailEntity.optionValue))
                .from(legacyProductEntity)
                .innerJoin(legacyProductStockEntity)
                .on(legacyProductStockEntity.productId.eq(legacyProductEntity.id))
                .leftJoin(legacyProductOptionEntity)
                .on(legacyProductOptionEntity.productId.eq(legacyProductEntity.id))
                .leftJoin(legacyOptionGroupEntity)
                .on(legacyOptionGroupEntity.id.eq(legacyProductOptionEntity.optionGroupId))
                .leftJoin(legacyOptionDetailEntity)
                .on(legacyOptionDetailEntity.id.eq(legacyProductOptionEntity.optionDetailId))
                .where(
                        legacyProductEntity.productGroupId.in(productGroupIds),
                        legacyProductEntity.deleteYn.eq("N"))
                .fetch();
    }

    /**
     * COUNT 쿼리: 검색 조건에 맞는 전체 건수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 건수
     */
    public long count(LegacyProductGroupSearchCriteria criteria) {
        Long result =
                queryFactory
                        .select(legacyProductGroupEntity.count())
                        .from(legacyProductGroupEntity)
                        .where(conditionBuilder.buildConditions(criteria))
                        .fetchOne();
        return result != null ? result : 0L;
    }

    private ConstructorExpression<LegacyProductGroupListQueryDto> createListProjection() {
        return Projections.constructor(
                LegacyProductGroupListQueryDto.class,
                legacyProductGroupEntity.id,
                legacyProductGroupEntity.productGroupName,
                legacyProductGroupEntity.sellerId,
                legacySellerEntity.sellerName,
                legacyProductGroupEntity.brandId,
                legacyBrandEntity.brandName,
                legacyProductGroupEntity.categoryId,
                legacyCategoryEntity.path,
                legacyProductGroupEntity.optionType,
                legacyProductGroupEntity.managementType,
                legacyProductGroupEntity.regularPrice,
                legacyProductGroupEntity.currentPrice,
                legacyProductGroupEntity.salePrice,
                new CaseBuilder()
                        .when(legacyProductGroupEntity.directDiscountPrice.isNull())
                        .then(0L)
                        .otherwise(legacyProductGroupEntity.directDiscountPrice),
                new CaseBuilder()
                        .when(legacyProductGroupEntity.directDiscountRate.isNull())
                        .then(0)
                        .otherwise(legacyProductGroupEntity.directDiscountRate),
                new CaseBuilder()
                        .when(legacyProductGroupEntity.discountRate.isNull())
                        .then(0)
                        .otherwise(legacyProductGroupEntity.discountRate),
                legacyProductGroupEntity.soldOutYn,
                legacyProductGroupEntity.displayYn,
                legacyProductGroupEntity.productCondition,
                legacyProductGroupEntity.origin,
                legacyProductGroupEntity.styleCode,
                legacyProductGroupEntity.insertOperator,
                legacyProductGroupEntity.updateOperator,
                legacyProductGroupEntity.insertDate,
                legacyProductGroupEntity.updateDate,
                legacyProductGroupImageEntity.imageUrl);
    }
}
