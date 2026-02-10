package com.ryuqq.marketplace.application.brandpreset.port.in.query;

import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;

/** 브랜드 프리셋 목록 조회 UseCase (Offset 기반 페이징). */
public interface SearchBrandPresetByOffsetUseCase {
    BrandPresetPageResult execute(BrandPresetSearchParams params);
}
