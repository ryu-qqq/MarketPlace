package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.QLegacyOptionDetailEntity.legacyOptionDetailEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.QLegacyOptionGroupEntity.legacyOptionGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductDeliveryEntity.legacyProductDeliveryEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductEntity.legacyProductEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductGroupDetailDescriptionEntity.legacyProductGroupDetailDescriptionEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductGroupEntity.legacyProductGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductGroupImageEntity.legacyProductGroupImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductNoticeEntity.legacyProductNoticeEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductOptionEntity.legacyProductOptionEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductStockEntity.legacyProductStockEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 상품그룹 QueryDSL 조회 Repository.
 *
 * <p>세토프 DB에서 상품그룹 및 관련 데이터를 배치 로딩 패턴으로 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class LegacyProductGroupQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyProductGroupQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<LegacyProductGroupEntity> findProductGroupById(long productGroupId) {
        LegacyProductGroupEntity result =
                queryFactory
                        .selectFrom(legacyProductGroupEntity)
                        .where(legacyProductGroupEntity.id.eq(productGroupId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<LegacyProductEntity> findProductsByProductGroupId(long productGroupId) {
        return queryFactory
                .selectFrom(legacyProductEntity)
                .where(
                        legacyProductEntity.productGroupId.eq(productGroupId),
                        legacyProductEntity.deleteYn.eq("N"))
                .fetch();
    }

    public List<LegacyProductOptionEntity> findProductOptionsByProductIds(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(legacyProductOptionEntity)
                .where(
                        legacyProductOptionEntity.productId.in(productIds),
                        legacyProductOptionEntity.deleteYn.eq("N"))
                .fetch();
    }

    public List<LegacyProductStockEntity> findStocksByProductIds(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(legacyProductStockEntity)
                .where(
                        legacyProductStockEntity.productId.in(productIds),
                        legacyProductStockEntity.deleteYn.eq("N"))
                .fetch();
    }

    public List<LegacyProductGroupImageEntity> findImagesByProductGroupId(long productGroupId) {
        return queryFactory
                .selectFrom(legacyProductGroupImageEntity)
                .where(
                        legacyProductGroupImageEntity.productGroupId.eq(productGroupId),
                        legacyProductGroupImageEntity.deleteYn.eq("N"))
                .fetch();
    }

    public Optional<LegacyProductGroupDetailDescriptionEntity>
            findDetailDescriptionByProductGroupId(long productGroupId) {
        LegacyProductGroupDetailDescriptionEntity result =
                queryFactory
                        .selectFrom(legacyProductGroupDetailDescriptionEntity)
                        .where(
                                legacyProductGroupDetailDescriptionEntity.productGroupId.eq(
                                        productGroupId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public Optional<LegacyProductNoticeEntity> findNoticeByProductGroupId(long productGroupId) {
        LegacyProductNoticeEntity result =
                queryFactory
                        .selectFrom(legacyProductNoticeEntity)
                        .where(legacyProductNoticeEntity.productGroupId.eq(productGroupId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public Optional<LegacyProductDeliveryEntity> findDeliveryByProductGroupId(long productGroupId) {
        LegacyProductDeliveryEntity result =
                queryFactory
                        .selectFrom(legacyProductDeliveryEntity)
                        .where(legacyProductDeliveryEntity.productGroupId.eq(productGroupId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<LegacyOptionGroupEntity> findOptionGroupsByIds(List<Long> optionGroupIds) {
        if (optionGroupIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(legacyOptionGroupEntity)
                .where(legacyOptionGroupEntity.id.in(optionGroupIds))
                .fetch();
    }

    public List<LegacyOptionDetailEntity> findOptionDetailsByIds(List<Long> optionDetailIds) {
        if (optionDetailIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(legacyOptionDetailEntity)
                .where(legacyOptionDetailEntity.id.in(optionDetailIds))
                .fetch();
    }
}
