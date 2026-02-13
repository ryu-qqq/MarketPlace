package com.ryuqq.marketplace.application.brandpreset.port.out.query;

import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import java.util.Optional;

/** BrandPreset Composition 조회 Port (크로스 도메인 조인). */
public interface BrandPresetCompositionQueryPort {

    Optional<BrandPresetDetailResult> findDetailById(Long id);
}
