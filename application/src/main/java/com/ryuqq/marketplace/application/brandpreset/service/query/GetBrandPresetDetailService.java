package com.ryuqq.marketplace.application.brandpreset.service.query;

import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetCompositionReadManager;
import com.ryuqq.marketplace.application.brandpreset.port.in.query.GetBrandPresetDetailUseCase;
import org.springframework.stereotype.Service;

/** 브랜드 프리셋 상세 조회 Service. */
@Service
public class GetBrandPresetDetailService implements GetBrandPresetDetailUseCase {

    private final BrandPresetCompositionReadManager compositionReadManager;

    public GetBrandPresetDetailService(BrandPresetCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    @Override
    public BrandPresetDetailResult execute(Long brandPresetId) {
        return compositionReadManager.getDetail(brandPresetId);
    }
}
