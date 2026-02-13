package com.ryuqq.marketplace.application.categorypreset.port.out.query;

import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import java.util.Optional;

/** CategoryPreset Composition 조회 Port (크로스 도메인 조인). */
public interface CategoryPresetCompositionQueryPort {

    Optional<CategoryPresetDetailResult> findDetailById(Long id);
}
