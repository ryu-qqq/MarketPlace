package com.ryuqq.marketplace.application.seller.port.in.command;

import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;

/** 셀러 전체 수정 UseCase. */
public interface UpdateSellerFullUseCase {

    void execute(UpdateSellerFullCommand command);
}
