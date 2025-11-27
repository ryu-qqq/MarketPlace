package com.ryuqq.marketplace.application.brand.port.in.command;

import com.ryuqq.marketplace.application.brand.dto.command.CreateBrandCommand;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResponse;

public interface CreateBrandUseCase {
    BrandResponse execute(CreateBrandCommand command);
}
