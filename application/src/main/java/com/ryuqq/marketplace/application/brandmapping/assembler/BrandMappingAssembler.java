package com.ryuqq.marketplace.application.brandmapping.assembler;

import com.ryuqq.marketplace.application.brandmapping.dto.response.BrandMappingPageResult;
import com.ryuqq.marketplace.application.brandmapping.dto.response.BrandMappingResult;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandMapping Assembler. */
@Component
public class BrandMappingAssembler {

    public BrandMappingResult toResult(BrandMapping brandMapping) {
        return BrandMappingResult.from(brandMapping);
    }

    public List<BrandMappingResult> toResults(List<BrandMapping> brandMappings) {
        return brandMappings.stream().map(this::toResult).toList();
    }

    public BrandMappingPageResult toPageResult(
            List<BrandMapping> brandMappings, int page, int size, long totalElements) {
        List<BrandMappingResult> results = toResults(brandMappings);
        return BrandMappingPageResult.of(results, page, size, totalElements);
    }
}
