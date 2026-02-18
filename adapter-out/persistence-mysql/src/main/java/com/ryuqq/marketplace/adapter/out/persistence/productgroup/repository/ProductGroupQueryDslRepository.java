package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition.ProductGroupConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QSellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QSellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.QProductGroupImageJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** ProductGroup QueryDSL Repository. */
@Repository
public class ProductGroupQueryDslRepository {

    private static final QProductGroupJpaEntity productGroup =
            QProductGroupJpaEntity.productGroupJpaEntity;
    private static final QProductGroupImageJpaEntity productGroupImage =
            QProductGroupImageJpaEntity.productGroupImageJpaEntity;
    private static final QSellerOptionGroupJpaEntity sellerOptionGroup =
            QSellerOptionGroupJpaEntity.sellerOptionGroupJpaEntity;
    private static final QSellerOptionValueJpaEntity sellerOptionValue =
            QSellerOptionValueJpaEntity.sellerOptionValueJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ProductGroupConditionBuilder conditionBuilder;

    public ProductGroupQueryDslRepository(
            JPAQueryFactory queryFactory, ProductGroupConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ProductGroupJpaEntity> findById(Long id) {
        ProductGroupJpaEntity entity =
                queryFactory
                        .selectFrom(productGroup)
                        .where(conditionBuilder.idEq(id), conditionBuilder.statusNotDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ProductGroupJpaEntity> findByCriteria(ProductGroupSearchCriteria criteria) {
        return queryFactory
                .selectFrom(productGroup)
                .where(
                        conditionBuilder.idIn(criteria.productGroupIds()),
                        conditionBuilder.sellerIdIn(criteria.sellerIds()),
                        conditionBuilder.brandIdIn(criteria.brandIds()),
                        conditionBuilder.categoryIdIn(criteria.categoryIds()),
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.statusNotDeleted())
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ProductGroupSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(productGroup.count())
                        .from(productGroup)
                        .where(
                                conditionBuilder.idIn(criteria.productGroupIds()),
                                conditionBuilder.sellerIdIn(criteria.sellerIds()),
                                conditionBuilder.brandIdIn(criteria.brandIds()),
                                conditionBuilder.categoryIdIn(criteria.categoryIds()),
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.statusNotDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public List<ProductGroupJpaEntity> findByIdsAndSellerId(List<Long> ids, Long sellerId) {
        return queryFactory
                .selectFrom(productGroup)
                .where(
                        conditionBuilder.idIn(ids),
                        conditionBuilder.sellerIdIn(List.of(sellerId)),
                        conditionBuilder.statusNotDeleted())
                .fetch();
    }

    public List<ProductGroupImageJpaEntity> findImagesByProductGroupId(Long productGroupId) {
        return queryFactory
                .selectFrom(productGroupImage)
                .where(productGroupImage.productGroupId.eq(productGroupId))
                .orderBy(productGroupImage.sortOrder.asc())
                .fetch();
    }

    public List<ProductGroupImageJpaEntity> findImagesByProductGroupIds(
            List<Long> productGroupIds) {
        return queryFactory
                .selectFrom(productGroupImage)
                .where(productGroupImage.productGroupId.in(productGroupIds))
                .orderBy(productGroupImage.sortOrder.asc())
                .fetch();
    }

    public List<SellerOptionGroupJpaEntity> findOptionGroupsByProductGroupId(Long productGroupId) {
        return queryFactory
                .selectFrom(sellerOptionGroup)
                .where(sellerOptionGroup.productGroupId.eq(productGroupId))
                .orderBy(sellerOptionGroup.sortOrder.asc())
                .fetch();
    }

    public List<SellerOptionGroupJpaEntity> findOptionGroupsByProductGroupIds(
            List<Long> productGroupIds) {
        return queryFactory
                .selectFrom(sellerOptionGroup)
                .where(sellerOptionGroup.productGroupId.in(productGroupIds))
                .orderBy(sellerOptionGroup.sortOrder.asc())
                .fetch();
    }

    public List<SellerOptionValueJpaEntity> findOptionValuesByOptionGroupIds(
            List<Long> optionGroupIds) {
        if (optionGroupIds == null || optionGroupIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(sellerOptionValue)
                .where(sellerOptionValue.sellerOptionGroupId.in(optionGroupIds))
                .orderBy(sellerOptionValue.sortOrder.asc())
                .fetch();
    }

    private OrderSpecifier<?> resolveOrderSpecifier(ProductGroupSearchCriteria criteria) {
        ProductGroupSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction.isAscending();

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? productGroup.createdAt.asc() : productGroup.createdAt.desc();
            case UPDATED_AT -> isAsc ? productGroup.updatedAt.asc() : productGroup.updatedAt.desc();
            case NAME ->
                    isAsc
                            ? productGroup.productGroupName.asc()
                            : productGroup.productGroupName.desc();
        };
    }
}
