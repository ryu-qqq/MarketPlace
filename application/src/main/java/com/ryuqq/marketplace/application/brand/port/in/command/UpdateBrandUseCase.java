package com.ryuqq.marketplace.application.brand.port.in.command;

import com.ryuqq.marketplace.application.brand.dto.command.UpdateBrandCommand;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResponse;

public interface UpdateBrandUseCase {
    BrandResponse execute(UpdateBrandCommand command);
}
