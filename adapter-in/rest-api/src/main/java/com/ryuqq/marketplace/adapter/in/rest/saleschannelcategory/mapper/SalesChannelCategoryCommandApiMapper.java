package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper;

import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.command.RegisterSalesChannelCategoryApiRequest;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;
import org.springframework.stereotype.Component;

/** SalesChannelCategory Command API Mapper. */
@Component
public class SalesChannelCategoryCommandApiMapper {

    public RegisterSalesChannelCategoryCommand toCommand(
            Long salesChannelId, RegisterSalesChannelCategoryApiRequest request) {
        return new RegisterSalesChannelCategoryCommand(
                salesChannelId,
                request.externalCategoryCode(),
                request.externalCategoryName(),
                request.parentId(),
                request.depth(),
                request.path(),
                request.sortOrder(),
                request.leaf(),
                request.displayPath());
    }
}
