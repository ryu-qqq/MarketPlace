package com.ryuqq.marketplace.domain.legacy.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 레거시 상품 그룹을 찾을 수 없을 때 발생하는 예외. */
public class LegacyProductGroupNotFoundException extends DomainException {

    public LegacyProductGroupNotFoundException(Long productGroupId) {
        super(
                LegacyProductGroupErrorCode.LEGACY_PRODUCT_GROUP_NOT_FOUND,
                String.format("레거시 상품 그룹을 찾을 수 없습니다: %d", productGroupId),
                Map.of("productGroupId", productGroupId));
    }
}
