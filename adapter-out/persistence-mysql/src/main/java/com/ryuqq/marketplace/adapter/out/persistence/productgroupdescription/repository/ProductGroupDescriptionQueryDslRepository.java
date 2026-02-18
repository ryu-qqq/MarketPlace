package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.condition.ProductGroupDescriptionConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.QDescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.QProductGroupDescriptionJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ProductGroupDescriptionQueryDslRepository - 상품 그룹 상세설명 QueryDSL Repository.
 *
 * <p>PER-ADP-001: QueryAdapter 전용 조회 Repository.
 */
@Repository
public class ProductGroupDescriptionQueryDslRepository {

    private static final QProductGroupDescriptionJpaEntity description =
            QProductGroupDescriptionJpaEntity.productGroupDescriptionJpaEntity;
    private static final QDescriptionImageJpaEntity image =
            QDescriptionImageJpaEntity.descriptionImageJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ProductGroupDescriptionConditionBuilder conditionBuilder;

    public ProductGroupDescriptionQueryDslRepository(
            JPAQueryFactory queryFactory,
            ProductGroupDescriptionConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ProductGroupDescriptionJpaEntity> findById(Long id) {
        ProductGroupDescriptionJpaEntity entity =
                queryFactory.selectFrom(description).where(description.id.eq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<ProductGroupDescriptionJpaEntity> findByProductGroupId(Long productGroupId) {
        ProductGroupDescriptionJpaEntity entity =
                queryFactory
                        .selectFrom(description)
                        .where(conditionBuilder.productGroupIdEq(productGroupId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ProductGroupDescriptionJpaEntity> findByPublishStatus(
            String publishStatus, int limit) {
        return queryFactory
                .selectFrom(description)
                .where(description.publishStatus.eq(publishStatus))
                .limit(limit)
                .fetch();
    }

    public List<DescriptionImageJpaEntity> findImagesByDescriptionId(Long descriptionId) {
        return queryFactory
                .selectFrom(image)
                .where(image.productGroupDescriptionId.eq(descriptionId), image.deleted.isFalse())
                .orderBy(image.sortOrder.asc())
                .fetch();
    }
}
