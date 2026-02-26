package com.ryuqq.marketplace.application.seller.port.in.command;

import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;

/** 셀러 기본정보 수정 UseCase. */
public interface UpdateSellerUseCase {

    void execute(UpdateSellerCommand command);
}
