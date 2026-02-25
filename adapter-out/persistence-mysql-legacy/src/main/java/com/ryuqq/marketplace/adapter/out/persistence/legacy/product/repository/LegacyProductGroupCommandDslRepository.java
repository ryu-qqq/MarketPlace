package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductEntity.legacyProductEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductGroupDetailDescriptionEntity.legacyProductGroupDetailDescriptionEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductGroupEntity.legacyProductGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductGroupImageEntity.legacyProductGroupImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductNoticeEntity.legacyProductNoticeEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductStockEntity.legacyProductStockEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 상품그룹 QueryDSL 커맨드 Repository.
 *
 * <p>세토프 DB의 상품그룹 및 관련 데이터를 직접 수정합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class LegacyProductGroupCommandDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyProductGroupCommandDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public long updatePrice(
            long productGroupId, long regularPrice, long currentPrice, long salePrice) {
        return queryFactory
                .update(legacyProductGroupEntity)
                .set(legacyProductGroupEntity.regularPrice, regularPrice)
                .set(legacyProductGroupEntity.currentPrice, currentPrice)
                .set(legacyProductGroupEntity.salePrice, salePrice)
                .where(legacyProductGroupEntity.id.eq(productGroupId))
                .execute();
    }

    public long updateDisplayYn(long productGroupId, String displayYn) {
        return queryFactory
                .update(legacyProductGroupEntity)
                .set(legacyProductGroupEntity.displayYn, displayYn)
                .where(legacyProductGroupEntity.id.eq(productGroupId))
                .execute();
    }

    public long updateSoldOutYn(long productGroupId, String soldOutYn) {
        return queryFactory
                .update(legacyProductGroupEntity)
                .set(legacyProductGroupEntity.soldOutYn, soldOutYn)
                .where(legacyProductGroupEntity.id.eq(productGroupId))
                .execute();
    }

    public long updateNotice(
            long productGroupId,
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {
        return queryFactory
                .update(legacyProductNoticeEntity)
                .set(legacyProductNoticeEntity.material, material)
                .set(legacyProductNoticeEntity.color, color)
                .set(legacyProductNoticeEntity.size, size)
                .set(legacyProductNoticeEntity.maker, maker)
                .set(legacyProductNoticeEntity.origin, origin)
                .set(legacyProductNoticeEntity.washingMethod, washingMethod)
                .set(legacyProductNoticeEntity.yearMonthDay, yearMonthDay)
                .set(legacyProductNoticeEntity.assuranceStandard, assuranceStandard)
                .set(legacyProductNoticeEntity.asPhone, asPhone)
                .where(legacyProductNoticeEntity.productGroupId.eq(productGroupId))
                .execute();
    }

    public long softDeleteImagesByProductGroupId(long productGroupId) {
        return queryFactory
                .update(legacyProductGroupImageEntity)
                .set(legacyProductGroupImageEntity.deleteYn, "Y")
                .where(legacyProductGroupImageEntity.productGroupId.eq(productGroupId))
                .execute();
    }

    public long updateDetailDescription(long productGroupId, String imageUrl) {
        return queryFactory
                .update(legacyProductGroupDetailDescriptionEntity)
                .set(legacyProductGroupDetailDescriptionEntity.imageUrl, imageUrl)
                .where(legacyProductGroupDetailDescriptionEntity.productGroupId.eq(productGroupId))
                .execute();
    }

    public long updateStock(long productId, int quantity) {
        return queryFactory
                .update(legacyProductStockEntity)
                .set(legacyProductStockEntity.stockQuantity, quantity)
                .where(legacyProductStockEntity.productId.eq(productId))
                .execute();
    }

    public long markProductSoldOut(long productId) {
        return queryFactory
                .update(legacyProductEntity)
                .set(legacyProductEntity.soldOutYn, "Y")
                .where(legacyProductEntity.id.eq(productId))
                .execute();
    }
}
