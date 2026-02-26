package com.ryuqq.marketplace.application.seller.port.in.command;

import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;

/** 셀러 등록 UseCase. */
public interface RegisterSellerUseCase {

    Long execute(RegisterSellerCommand command);
}
