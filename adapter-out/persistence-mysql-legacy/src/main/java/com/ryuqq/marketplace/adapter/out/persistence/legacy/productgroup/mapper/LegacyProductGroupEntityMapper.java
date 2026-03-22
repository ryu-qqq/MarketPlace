package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import org.springframework.stereotype.Component;

/** 레거시 상품그룹 Entity ↔ 표준 도메인 매퍼. */
@Component
public class LegacyProductGroupEntityMapper {

    public LegacyProductGroupEntity toEntity(
            ProductGroup productGroup, long regularPrice, long currentPrice) {
        return LegacyProductGroupEntity.create(
                productGroup.idValue(),
                productGroup.productGroupNameValue(),
                productGroup.sellerIdValue(),
                productGroup.brandIdValue(),
                productGroup.categoryIdValue(),
                productGroup.optionType().name(),
                "MENUAL",
                regularPrice,
                currentPrice,
                "N",
                "Y",
                "NEW",
                "OTHER",
                "");
    }
}
