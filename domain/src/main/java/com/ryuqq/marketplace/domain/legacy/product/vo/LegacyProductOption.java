package com.ryuqq.marketplace.domain.legacy.product.vo;

import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;

/** 레거시(세토프) 상품-옵션 매핑 Value Object. */
public record LegacyProductOption(
        Long id,
        LegacyProductId productId,
        LegacyOptionGroupId optionGroupId,
        LegacyOptionDetailId optionDetailId,
        long additionalPrice) {}
