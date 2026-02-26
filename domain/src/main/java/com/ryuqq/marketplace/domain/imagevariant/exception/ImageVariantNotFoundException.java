package com.ryuqq.marketplace.domain.imagevariant.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 이미지 Variant를 찾을 수 없을 때 발생하는 예외. */
public class ImageVariantNotFoundException extends DomainException {

    public ImageVariantNotFoundException(Long imageVariantId) {
        super(
                ImageVariantErrorCode.IMAGE_VARIANT_NOT_FOUND,
                String.format("이미지 Variant를 찾을 수 없습니다: %d", imageVariantId),
                Map.of("imageVariantId", imageVariantId));
    }
}
