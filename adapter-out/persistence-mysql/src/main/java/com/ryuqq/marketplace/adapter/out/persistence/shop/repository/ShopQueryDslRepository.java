package com.ryuqq.marketplace.adapter.out.persistence.shop.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.shop.condition.ShopConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import com.ryuqq.marketplace.domain.shop.query.ShopSortKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Shop QueryDSL Repository. */
@Repository
public class ShopQueryDslRepository {

    private static final QShopJpaEntity shop = QShopJpaEntity.shopJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ShopConditionBuilder conditionBuilder;

    public ShopQueryDslRepository(
            JPAQueryFactory queryFactory, ShopConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ShopJpaEntity> findById(Long id) {
        ShopJpaEntity entity =
                queryFactory
                        .selectFrom(shop)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ShopJpaEntity> findByCriteria(ShopSearchCriteria criteria) {
        return queryFactory
                .selectFrom(shop)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.notDeleted())
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ShopSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(shop.count())
                        .from(shop)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsByShopName(String shopName) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(shop)
                        .where(conditionBuilder.shopNameEq(shopName), conditionBuilder.notDeleted())
                        .fetchFirst();
        return count != null;
    }

    public boolean existsByShopNameExcluding(String shopName, Long excludeId) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(shop)
                        .where(
                                conditionBuilder.shopNameEq(shopName),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return count != null;
    }

    public boolean existsByAccountId(String accountId) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(shop)
                        .where(
                                conditionBuilder.accountIdEq(accountId),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return count != null;
    }

    public boolean existsByAccountIdExcluding(String accountId, Long excludeId) {
        Integer count =
                queryFactory
                        .selectOne()
                        .from(shop)
                        .where(
                                conditionBuilder.accountIdEq(accountId),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return count != null;
    }

    private OrderSpecifier<?> resolveOrderSpecifier(ShopSearchCriteria criteria) {
        ShopSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? shop.createdAt.asc() : shop.createdAt.desc();
            case UPDATED_AT -> isAsc ? shop.updatedAt.asc() : shop.updatedAt.desc();
            case SHOP_NAME -> isAsc ? shop.shopName.asc() : shop.shopName.desc();
        };
    }
}
