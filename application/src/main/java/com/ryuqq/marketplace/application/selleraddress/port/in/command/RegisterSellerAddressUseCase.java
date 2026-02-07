package com.ryuqq.marketplace.application.selleraddress.port.in.command;

import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;

/** 셀러 주소 등록 UseCase. */
public interface RegisterSellerAddressUseCase {

    Long execute(RegisterSellerAddressCommand command);
}
