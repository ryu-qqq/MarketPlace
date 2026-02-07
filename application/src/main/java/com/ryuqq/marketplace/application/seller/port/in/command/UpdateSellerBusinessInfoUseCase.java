package com.ryuqq.marketplace.application.seller.port.in.command;

import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerBusinessInfoCommand;

/** 사업자 정보 수정 UseCase. */
public interface UpdateSellerBusinessInfoUseCase {

    void execute(UpdateSellerBusinessInfoCommand command);
}
