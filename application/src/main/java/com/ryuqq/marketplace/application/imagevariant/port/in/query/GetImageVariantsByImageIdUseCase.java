package com.ryuqq.marketplace.application.imagevariant.port.in.query;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import java.util.List;

/** 이미지별 Variant 조회 UseCase. */
public interface GetImageVariantsByImageIdUseCase {

    List<ImageVariantResult> execute(Long sourceImageId);
}
