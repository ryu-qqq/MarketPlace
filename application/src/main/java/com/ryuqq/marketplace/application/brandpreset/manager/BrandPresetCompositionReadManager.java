package com.ryuqq.marketplace.application.brandpreset.manager;

import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import com.ryuqq.marketplace.application.brandpreset.port.out.query.BrandPresetCompositionQueryPort;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** BrandPreset Composition ReadManager. */
@Component
public class BrandPresetCompositionReadManager {

    private final BrandPresetCompositionQueryPort compositionQueryPort;

    public BrandPresetCompositionReadManager(BrandPresetCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    @Transactional(readOnly = true)
    public BrandPresetDetailResult getDetail(Long id) {
        return compositionQueryPort
                .findDetailById(id)
                .orElseThrow(() -> new BrandPresetNotFoundException(id));
    }
}
