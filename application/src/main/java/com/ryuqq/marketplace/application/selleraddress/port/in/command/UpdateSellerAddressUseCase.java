package com.ryuqq.marketplace.application.selleraddress.port.in.command;

import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;

/** 셀러 주소 수정 UseCase. */
public interface UpdateSellerAddressUseCase {

    void execute(UpdateSellerAddressCommand command);
}
