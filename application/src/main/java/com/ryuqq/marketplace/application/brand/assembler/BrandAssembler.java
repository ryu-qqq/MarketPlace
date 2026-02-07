package com.ryuqq.marketplace.application.brand.assembler;

import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResult;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import java.util.List;
import org.springframework.stereotype.Component;

/** Brand Assembler. */
@Component
public class BrandAssembler {

    public BrandResult toResult(Brand brand) {
        return BrandResult.from(brand);
    }

    public List<BrandResult> toResults(List<Brand> brands) {
        return brands.stream().map(this::toResult).toList();
    }

    public BrandPageResult toPageResult(
            List<Brand> brands, int page, int size, long totalElements) {
        List<BrandResult> results = toResults(brands);
        return BrandPageResult.of(results, page, size, totalElements);
    }
}
