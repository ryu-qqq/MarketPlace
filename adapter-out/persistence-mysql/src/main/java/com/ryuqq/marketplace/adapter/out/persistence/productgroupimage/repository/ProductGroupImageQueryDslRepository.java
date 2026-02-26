package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.QProductGroupImageJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ProductGroupImage QueryDSL Repository.
 *
 * <p>PER-ADP-001: QueryAdapter 전용 QueryDSL 레포지토리입니다.
 */
@Repository
public class ProductGroupImageQueryDslRepository {

    private static final QProductGroupImageJpaEntity productGroupImage =
            QProductGroupImageJpaEntity.productGroupImageJpaEntity;

    private final JPAQueryFactory queryFactory;

    public ProductGroupImageQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<ProductGroupImageJpaEntity> findById(Long id) {
        ProductGroupImageJpaEntity entity =
                queryFactory
                        .selectFrom(productGroupImage)
                        .where(productGroupImage.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ProductGroupImageJpaEntity> findByProductGroupId(Long productGroupId) {
        return queryFactory
                .selectFrom(productGroupImage)
                .where(
                        productGroupImage.productGroupId.eq(productGroupId),
                        productGroupImage.deleted.isFalse())
                .orderBy(productGroupImage.sortOrder.asc())
                .fetch();
    }

    public List<ProductGroupImageJpaEntity> findByProductGroupIdIn(List<Long> productGroupIds) {
        return queryFactory
                .selectFrom(productGroupImage)
                .where(
                        productGroupImage.productGroupId.in(productGroupIds),
                        productGroupImage.deleted.isFalse())
                .orderBy(productGroupImage.productGroupId.asc(), productGroupImage.sortOrder.asc())
                .fetch();
    }
}
