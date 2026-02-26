package com.ryuqq.marketplace.application.brandpreset.port.in.query;

import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;

/** 브랜드 프리셋 상세 조회 UseCase. */
public interface GetBrandPresetDetailUseCase {
    BrandPresetDetailResult execute(Long brandPresetId);
}
