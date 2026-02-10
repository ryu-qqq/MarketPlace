package com.ryuqq.marketplace.adapter.in.rest.categorymapping.mapper;

import com.ryuqq.marketplace.adapter.in.rest.categorymapping.dto.command.RegisterCategoryMappingApiRequest;
import com.ryuqq.marketplace.application.categorymapping.dto.command.RegisterCategoryMappingCommand;
import org.springframework.stereotype.Component;

/** CategoryMapping Command API Mapper. */
@Component
public class CategoryMappingCommandApiMapper {

    public RegisterCategoryMappingCommand toCommand(RegisterCategoryMappingApiRequest request) {
        return new RegisterCategoryMappingCommand(
                request.salesChannelCategoryId(), request.internalCategoryId());
    }
}
