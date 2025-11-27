package com.ryuqq.marketplace.application.brand.port.in.command;

import com.ryuqq.marketplace.application.brand.dto.command.AddBrandAliasCommand;
import com.ryuqq.marketplace.application.brand.dto.response.BrandAliasResponse;

public interface AddBrandAliasUseCase {
    BrandAliasResponse execute(AddBrandAliasCommand command);
}
