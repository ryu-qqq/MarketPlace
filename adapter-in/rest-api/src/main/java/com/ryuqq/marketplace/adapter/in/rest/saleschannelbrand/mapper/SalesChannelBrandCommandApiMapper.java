package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper;

import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.command.RegisterSalesChannelBrandApiRequest;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;
import org.springframework.stereotype.Component;

/** SalesChannelBrand Command API Mapper. */
@Component
public class SalesChannelBrandCommandApiMapper {

    public RegisterSalesChannelBrandCommand toCommand(
            Long salesChannelId, RegisterSalesChannelBrandApiRequest request) {
        return new RegisterSalesChannelBrandCommand(
                salesChannelId, request.externalBrandCode(), request.externalBrandName());
    }
}
