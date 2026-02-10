package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.QCategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.QSalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryPreset QueryDSL 조건 빌더. */
@Component
public class CategoryPresetConditionBuilder {

    private static final QCategoryPresetJpaEntity categoryPreset =
            QCategoryPresetJpaEntity.categoryPresetJpaEntity;
    private static final QShopJpaEntity shop = QShopJpaEntity.shopJpaEntity;
    private static final QSalesChannelCategoryJpaEntity salesChannelCategory =
            QSalesChannelCategoryJpaEntity.salesChannelCategoryJpaEntity;
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    public BooleanExpression idEq(Long id) {
        return id != null ? categoryPreset.id.eq(id) : null;
    }

    public BooleanExpression idsIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? categoryPreset.id.in(ids) : null;
    }

    public BooleanExpression salesChannelIdsIn(CategoryPresetSearchCriteria criteria) {
        if (!criteria.hasSalesChannelFilter()) {
            return null;
        }
        return shop.salesChannelId.in(criteria.salesChannelIds());
    }

    public BooleanExpression statusesIn(CategoryPresetSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        return categoryPreset.status.in(criteria.statuses());
    }

    public BooleanExpression searchCondition(CategoryPresetSearchCriteria criteria) {
        if (!criteria.hasSearchFilter()) {
            return null;
        }
        String field = criteria.searchField();
        String word = criteria.searchWord();

        return switch (field) {
            case "PRESET_NAME" -> categoryPreset.presetName.containsIgnoreCase(word);
            case "SHOP_NAME" -> shop.shopName.containsIgnoreCase(word);
            case "ACCOUNT_ID" -> shop.accountId.containsIgnoreCase(word);
            case "CATEGORY_CODE" ->
                    salesChannelCategory.externalCategoryCode.containsIgnoreCase(word);
            case "CATEGORY_PATH" -> salesChannelCategory.displayPath.containsIgnoreCase(word);
            default -> null;
        };
    }

    public BooleanExpression createdAtGoe(CategoryPresetSearchCriteria criteria) {
        if (!criteria.hasStartDateFilter()) {
            return null;
        }
        LocalDate startDate = criteria.startDate();
        return categoryPreset.createdAt.goe(startDate.atStartOfDay(ZONE_ID).toInstant());
    }

    public BooleanExpression createdAtLoe(CategoryPresetSearchCriteria criteria) {
        if (!criteria.hasEndDateFilter()) {
            return null;
        }
        LocalDate endDate = criteria.endDate();
        return categoryPreset.createdAt.loe(
                endDate.atTime(LocalTime.MAX).atZone(ZONE_ID).toInstant());
    }

    public BooleanExpression statusActive() {
        return categoryPreset.status.eq("ACTIVE");
    }
}
