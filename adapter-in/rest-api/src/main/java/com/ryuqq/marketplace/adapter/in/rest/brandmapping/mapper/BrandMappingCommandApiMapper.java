package com.ryuqq.marketplace.adapter.in.rest.brandmapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.brandmapping.dto.command.RegisterBrandMappingApiRequest;
import com.ryuqq.marketplace.application.brandmapping.dto.command.RegisterBrandMappingCommand;
import org.springframework.stereotype.Component;

/** BrandMapping Command API Mapper. */
@Component
public class BrandMappingCommandApiMapper {

    public RegisterBrandMappingCommand toCommand(RegisterBrandMappingApiRequest request) {
        return new RegisterBrandMappingCommand(
                request.salesChannelBrandId(), request.internalBrandId());
    }
}
