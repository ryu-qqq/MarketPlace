package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.QProductGroupJpaEntity.productGroupJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerJpaEntity.sellerJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchField;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OmsProductConditionBuilder - OMS 상품 조회 QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class OmsProductConditionBuilder {

    public BooleanExpression statusIn(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return productGroupJpaEntity.status.in(statuses);
    }

    public BooleanExpression sellerIdIn(List<Long> sellerIds) {
        if (sellerIds == null || sellerIds.isEmpty()) {
            return null;
        }
        return productGroupJpaEntity.sellerId.in(sellerIds);
    }

    public BooleanExpression productGroupIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return productGroupJpaEntity.id.in(ids);
    }

    public BooleanExpression productGroupNameContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return productGroupJpaEntity.productGroupName.containsIgnoreCase(keyword);
    }

    /**
     * 검색 필드 기반 검색 조건.
     *
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return 검색 조건
     */
    public BooleanExpression searchFieldContains(
            OmsProductSearchField searchField, String searchWord) {
        if (searchWord == null || searchWord.isBlank()) {
            return null;
        }
        if (searchField == null) {
            return productGroupJpaEntity
                    .productGroupName
                    .containsIgnoreCase(searchWord)
                    .or(sellerJpaEntity.sellerName.containsIgnoreCase(searchWord));
        }
        return switch (searchField) {
            case PRODUCT_CODE -> {
                String prefix = "PG-";
                if (searchWord.toUpperCase().startsWith(prefix)) {
                    String idPart = searchWord.substring(prefix.length());
                    try {
                        long pgId = Long.parseLong(idPart);
                        yield productGroupJpaEntity.id.eq(pgId);
                    } catch (NumberFormatException e) {
                        yield null;
                    }
                }
                try {
                    long pgId = Long.parseLong(searchWord);
                    yield productGroupJpaEntity.id.eq(pgId);
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            case PRODUCT_NAME ->
                    productGroupJpaEntity.productGroupName.containsIgnoreCase(searchWord);
            case PARTNER_NAME -> sellerJpaEntity.sellerName.containsIgnoreCase(searchWord);
        };
    }

    public BooleanExpression createdAtBetween(Instant start, Instant end) {
        if (start == null && end == null) {
            return null;
        }
        if (start != null && end != null) {
            return productGroupJpaEntity.createdAt.between(start, end);
        }
        if (start != null) {
            return productGroupJpaEntity.createdAt.goe(start);
        }
        return productGroupJpaEntity.createdAt.loe(end);
    }

    public BooleanExpression updatedAtBetween(Instant start, Instant end) {
        if (start == null && end == null) {
            return null;
        }
        if (start != null && end != null) {
            return productGroupJpaEntity.updatedAt.between(start, end);
        }
        if (start != null) {
            return productGroupJpaEntity.updatedAt.goe(start);
        }
        return productGroupJpaEntity.updatedAt.loe(end);
    }

    public BooleanExpression notDeleted() {
        return productGroupJpaEntity.status.ne("DELETED");
    }
}
