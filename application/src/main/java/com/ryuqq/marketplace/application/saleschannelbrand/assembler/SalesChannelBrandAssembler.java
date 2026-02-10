package com.ryuqq.marketplace.application.saleschannelbrand.assembler;

import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandResult;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelBrand Assembler. */
@Component
public class SalesChannelBrandAssembler {

    public SalesChannelBrandResult toResult(SalesChannelBrand brand) {
        return SalesChannelBrandResult.from(brand);
    }

    public List<SalesChannelBrandResult> toResults(List<SalesChannelBrand> brands) {
        return brands.stream().map(this::toResult).toList();
    }

    public SalesChannelBrandPageResult toPageResult(
            List<SalesChannelBrand> brands, int page, int size, long totalElements) {
        List<SalesChannelBrandResult> results = toResults(brands);
        return SalesChannelBrandPageResult.of(results, page, size, totalElements);
    }
}
