package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.QLegacyOptionDetailEntity.legacyOptionDetailEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.QLegacyOptionGroupEntity.legacyOptionGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductEntity.legacyProductEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductOptionEntity.legacyProductOptionEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.QLegacyProductStockEntity.legacyProductStockEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 세토프 DB 상품 Composite QueryDSL Repository.
 *
 * <p>Product + ProductStock + ProductOption + OptionGroup + OptionDetail 5테이블 조인을 통해 상품 단위
 * Composite 데이터를 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class LegacyProductCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyProductCompositeQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 상품그룹 ID로 상품+옵션+재고 flat 데이터 조회.
     *
     * <p>한 상품이 여러 옵션을 가지면 여러 행으로 반환됩니다. 옵션이 없는 상품도 LEFT JOIN으로 포함됩니다.
     *
     * @param productGroupId 세토프 상품그룹 ID
     * @return flat projection 목록
     */
    public List<LegacyProductOptionQueryDto> fetchProductsWithOptions(long productGroupId) {
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
                        legacyProductEntity.productGroupId.eq(productGroupId),
                        legacyProductEntity.deleteYn.eq("N"))
                .fetch();
    }
}
