package com.ryuqq.marketplace.application.brandpreset.assembler;

import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandPreset Assembler. */
@Component
public class BrandPresetAssembler {

    public BrandPresetPageResult toPageResult(
            List<BrandPresetResult> results, int page, int size, long totalElements) {
        return BrandPresetPageResult.of(results, page, size, totalElements);
    }
}
