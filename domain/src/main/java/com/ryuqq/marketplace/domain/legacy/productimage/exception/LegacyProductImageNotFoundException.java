package com.ryuqq.marketplace.domain.legacy.productimage.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 레거시 상품 이미지를 찾을 수 없을 때 발생하는 예외. */
public class LegacyProductImageNotFoundException extends DomainException {

    public LegacyProductImageNotFoundException(Long imageId) {
        super(
                LegacyProductImageErrorCode.LEGACY_PRODUCT_IMAGE_NOT_FOUND,
                String.format("레거시 상품 이미지를 찾을 수 없습니다: %d", imageId),
                Map.of("imageId", imageId));
    }
}
