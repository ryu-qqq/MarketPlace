package com.ryuqq.marketplace.adapter.out.persistence.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.product.condition.ProductConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.QProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.QProductOptionMappingJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Product QueryDSL Repository. PER-ADP-001: QueryAdapter 전용. */
@Repository
public class ProductQueryDslRepository {

    private static final QProductJpaEntity product = QProductJpaEntity.productJpaEntity;
    private static final QProductOptionMappingJpaEntity optionMapping =
            QProductOptionMappingJpaEntity.productOptionMappingJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ProductConditionBuilder conditionBuilder;

    public ProductQueryDslRepository(
            JPAQueryFactory queryFactory, ProductConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ProductJpaEntity> findById(Long id) {
        ProductJpaEntity entity =
                queryFactory
                        .selectFrom(product)
                        .where(conditionBuilder.idEq(id), conditionBuilder.statusNotDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ProductJpaEntity> findByProductGroupId(Long productGroupId) {
        return queryFactory
                .selectFrom(product)
                .where(
                        conditionBuilder.productGroupIdEq(productGroupId),
                        conditionBuilder.statusNotDeleted())
                .fetch();
    }

    public List<ProductJpaEntity> findByProductGroupIdAndIdIn(
            Long productGroupId, List<Long> productIds) {
        return queryFactory
                .selectFrom(product)
                .where(
                        conditionBuilder.productGroupIdEq(productGroupId),
                        conditionBuilder.idIn(productIds),
                        conditionBuilder.statusNotDeleted())
                .fetch();
    }

    public List<ProductJpaEntity> findByIdIn(List<Long> ids) {
        return queryFactory
                .selectFrom(product)
                .where(conditionBuilder.idIn(ids), conditionBuilder.statusNotDeleted())
                .fetch();
    }

    public List<ProductJpaEntity> findByProductGroupIdIn(List<Long> productGroupIds) {
        return queryFactory
                .selectFrom(product)
                .where(
                        conditionBuilder.productGroupIdIn(productGroupIds),
                        conditionBuilder.statusNotDeleted())
                .fetch();
    }

    public List<ProductOptionMappingJpaEntity> findOptionMappingsByProductId(Long productId) {
        return queryFactory
                .selectFrom(optionMapping)
                .where(optionMapping.productId.eq(productId), optionMapping.deleted.isFalse())
                .fetch();
    }

    public List<ProductOptionMappingJpaEntity> findOptionMappingsByProductIds(
            List<Long> productIds) {
        return queryFactory
                .selectFrom(optionMapping)
                .where(optionMapping.productId.in(productIds), optionMapping.deleted.isFalse())
                .fetch();
    }
}
