package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.brand.entity.QLegacyBrandEntity.legacyBrandEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.category.entity.QLegacyCategoryEntity.legacyCategoryEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.entity.QLegacyProductDeliveryEntity.legacyProductDeliveryEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.QLegacyProductGroupDetailDescriptionEntity.legacyProductGroupDetailDescriptionEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.QLegacyProductGroupEntity.legacyProductGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.QLegacyProductGroupImageEntity.legacyProductGroupImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.entity.QLegacyProductNoticeEntity.legacyProductNoticeEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.seller.entity.QLegacySellerEntity.legacySellerEntity;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupBasicQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupImageQueryDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 상품그룹 상세 조회 QueryDSL Repository.
 *
 * <p>Query 1: 기본 정보 (7개 테이블 조인). Query 2: 이미지 목록.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Repository
public class LegacyProductGroupDetailQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyProductGroupDetailQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 상품그룹 기본 정보 조회.
     *
     * <p>7개 테이블 조인: product_group, seller, brand, category, product_delivery,
     * product_group_detail_description, product_notice
     */
    public Optional<LegacyProductGroupBasicQueryDto> fetchBasicInfo(long productGroupId) {
        LegacyProductGroupBasicQueryDto dto =
                queryFactory
                        .select(createBasicProjection())
                        .from(legacyProductGroupEntity)
                        .innerJoin(legacySellerEntity)
                        .on(legacySellerEntity.id.eq(legacyProductGroupEntity.sellerId))
                        .innerJoin(legacyBrandEntity)
                        .on(legacyBrandEntity.id.eq(legacyProductGroupEntity.brandId))
                        .leftJoin(legacyCategoryEntity)
                        .on(legacyCategoryEntity.id.eq(legacyProductGroupEntity.categoryId))
                        .leftJoin(legacyProductDeliveryEntity)
                        .on(
                                legacyProductDeliveryEntity.productGroupId.eq(
                                        legacyProductGroupEntity.id))
                        .leftJoin(legacyProductGroupDetailDescriptionEntity)
                        .on(
                                legacyProductGroupDetailDescriptionEntity.productGroupId.eq(
                                        legacyProductGroupEntity.id))
                        .leftJoin(legacyProductNoticeEntity)
                        .on(
                                legacyProductNoticeEntity.productGroupId.eq(
                                        legacyProductGroupEntity.id))
                        .where(
                                legacyProductGroupEntity.id.eq(productGroupId),
                                legacyProductGroupEntity.deleteYn.eq("N"))
                        .fetchFirst();
        return Optional.ofNullable(dto);
    }

    /** 상품그룹 이미지 목록 조회. */
    public List<LegacyProductGroupImageQueryDto> fetchImages(long productGroupId) {
        return queryFactory
                .select(createImageProjection())
                .from(legacyProductGroupImageEntity)
                .where(
                        legacyProductGroupImageEntity.productGroupId.eq(productGroupId),
                        legacyProductGroupImageEntity.deleteYn.eq("N"))
                .fetch();
    }

    private ConstructorExpression<LegacyProductGroupBasicQueryDto> createBasicProjection() {
        return Projections.constructor(
                LegacyProductGroupBasicQueryDto.class,
                // product_group
                legacyProductGroupEntity.id,
                legacyProductGroupEntity.productGroupName,
                legacyProductGroupEntity.sellerId,
                legacyProductGroupEntity.brandId,
                legacyProductGroupEntity.categoryId,
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
                // seller
                legacySellerEntity.sellerName,
                // brand
                legacyBrandEntity.brandName,
                // category
                legacyCategoryEntity.path,
                // delivery
                legacyProductDeliveryEntity.deliveryArea,
                legacyProductDeliveryEntity.deliveryFee,
                legacyProductDeliveryEntity.deliveryPeriodAverage,
                legacyProductDeliveryEntity.returnMethodDomestic,
                legacyProductDeliveryEntity.returnCourierDomestic,
                legacyProductDeliveryEntity.returnChargeDomestic,
                legacyProductDeliveryEntity.returnExchangeAreaDomestic,
                // description
                legacyProductGroupDetailDescriptionEntity.imageUrl,
                // notice
                legacyProductNoticeEntity.material,
                legacyProductNoticeEntity.color,
                legacyProductNoticeEntity.size,
                legacyProductNoticeEntity.maker,
                legacyProductNoticeEntity.origin,
                legacyProductNoticeEntity.washingMethod,
                legacyProductNoticeEntity.yearMonthDay,
                legacyProductNoticeEntity.assuranceStandard,
                legacyProductNoticeEntity.asPhone);
    }

    private ConstructorExpression<LegacyProductGroupImageQueryDto> createImageProjection() {
        return Projections.constructor(
                LegacyProductGroupImageQueryDto.class,
                legacyProductGroupImageEntity.productGroupImageType,
                legacyProductGroupImageEntity.imageUrl);
    }
}
