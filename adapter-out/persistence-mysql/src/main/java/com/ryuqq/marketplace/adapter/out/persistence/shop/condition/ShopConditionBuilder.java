package com.ryuqq.marketplace.adapter.out.persistence.shop.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shop QueryDSL 조건 빌더. */
@Component
public class ShopConditionBuilder {

    private static final QShopJpaEntity shop = QShopJpaEntity.shopJpaEntity;

    public BooleanExpression idEq(Long id) {
        return id != null ? shop.id.eq(id) : null;
    }

    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? shop.id.ne(excludeId) : null;
    }

    public BooleanExpression shopNameEq(String shopName) {
        return shopName != null ? shop.shopName.eq(shopName) : null;
    }

    public BooleanExpression salesChannelIdEq(Long salesChannelId) {
        return salesChannelId != null ? shop.salesChannelId.eq(salesChannelId) : null;
    }

    public BooleanExpression accountIdEq(String accountId) {
        return accountId != null ? shop.accountId.eq(accountId) : null;
    }

    public BooleanExpression statusIn(ShopSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(ShopStatus::name).toList();
        return shop.status.in(statusNames);
    }

    public BooleanExpression searchCondition(ShopSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return shop.shopName.like(word).or(shop.accountId.like(word));
        }
        return switch (criteria.searchField()) {
            case SHOP_NAME -> shop.shopName.like(word);
            case ACCOUNT_ID -> shop.accountId.like(word);
        };
    }

    public BooleanExpression notDeleted() {
        return shop.deletedAt.isNull();
    }
}
