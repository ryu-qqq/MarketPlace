package com.ryuqq.marketplace.application.selleraddress.port.in.command;

import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;

/** 셀러 주소 삭제 UseCase. */
public interface DeleteSellerAddressUseCase {

    void execute(DeleteSellerAddressCommand command);
}
