package com.ryuqq.marketplace.application.saleschannelcategory.assembler;

import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryResult;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelCategory Assembler. */
@Component
public class SalesChannelCategoryAssembler {

    public SalesChannelCategoryResult toResult(SalesChannelCategory category) {
        return SalesChannelCategoryResult.from(category);
    }

    public List<SalesChannelCategoryResult> toResults(List<SalesChannelCategory> categories) {
        return categories.stream().map(this::toResult).toList();
    }

    public SalesChannelCategoryPageResult toPageResult(
            List<SalesChannelCategory> categories, int page, int size, long totalElements) {
        List<SalesChannelCategoryResult> results = toResults(categories);
        return SalesChannelCategoryPageResult.of(results, page, size, totalElements);
    }
}
