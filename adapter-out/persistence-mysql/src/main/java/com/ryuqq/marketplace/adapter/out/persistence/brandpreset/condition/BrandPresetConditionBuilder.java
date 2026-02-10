package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.QBrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.QSalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.QShopJpaEntity;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandPreset QueryDSL 조건 빌더. */
@Component
public class BrandPresetConditionBuilder {

    private static final QBrandPresetJpaEntity brandPreset =
            QBrandPresetJpaEntity.brandPresetJpaEntity;
    private static final QShopJpaEntity shop = QShopJpaEntity.shopJpaEntity;
    private static final QSalesChannelBrandJpaEntity salesChannelBrand =
            QSalesChannelBrandJpaEntity.salesChannelBrandJpaEntity;
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    public BooleanExpression idEq(Long id) {
        return id != null ? brandPreset.id.eq(id) : null;
    }

    public BooleanExpression idsIn(List<Long> ids) {
        return ids != null && !ids.isEmpty() ? brandPreset.id.in(ids) : null;
    }

    public BooleanExpression salesChannelIdsIn(BrandPresetSearchCriteria criteria) {
        if (!criteria.hasSalesChannelFilter()) {
            return null;
        }
        return salesChannelBrand.salesChannelId.in(criteria.salesChannelIds());
    }

    public BooleanExpression statusesIn(BrandPresetSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        return brandPreset.status.in(criteria.statuses());
    }

    public BooleanExpression searchCondition(BrandPresetSearchCriteria criteria) {
        if (!criteria.hasSearchFilter()) {
            return null;
        }
        String field = criteria.searchField();
        String word = criteria.searchWord();

        return switch (field) {
            case "PRESET_NAME" -> brandPreset.presetName.containsIgnoreCase(word);
            case "SHOP_NAME" -> shop.shopName.containsIgnoreCase(word);
            case "ACCOUNT_ID" -> shop.accountId.containsIgnoreCase(word);
            case "BRAND_NAME" -> salesChannelBrand.externalBrandName.containsIgnoreCase(word);
            case "BRAND_CODE" -> salesChannelBrand.externalBrandCode.containsIgnoreCase(word);
            default -> null;
        };
    }

    public BooleanExpression createdAtGoe(BrandPresetSearchCriteria criteria) {
        if (!criteria.hasStartDateFilter()) {
            return null;
        }
        LocalDate startDate = criteria.startDate();
        return brandPreset.createdAt.goe(startDate.atStartOfDay(ZONE_ID).toInstant());
    }

    public BooleanExpression createdAtLoe(BrandPresetSearchCriteria criteria) {
        if (!criteria.hasEndDateFilter()) {
            return null;
        }
        LocalDate endDate = criteria.endDate();
        return brandPreset.createdAt.loe(endDate.atTime(LocalTime.MAX).atZone(ZONE_ID).toInstant());
    }

    public BooleanExpression statusActive() {
        return brandPreset.status.eq("ACTIVE");
    }
}
