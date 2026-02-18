package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 상세설명 이미지를 찾을 수 없을 때 발생하는 예외. */
public class DescriptionImageNotFoundException extends DomainException {

    public DescriptionImageNotFoundException(Long imageId) {
        super(
                ProductGroupErrorCode.DESCRIPTION_IMAGE_NOT_FOUND,
                String.format("상세설명 이미지를 찾을 수 없습니다: %d", imageId),
                Map.of("imageId", imageId));
    }
}
