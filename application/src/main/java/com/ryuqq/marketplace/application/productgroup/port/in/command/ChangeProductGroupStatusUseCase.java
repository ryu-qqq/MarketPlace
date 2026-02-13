package com.ryuqq.marketplace.application.productgroup.port.in.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.ChangeProductGroupStatusCommand;

/** 상품 그룹 상태 변경 UseCase. */
public interface ChangeProductGroupStatusUseCase {

    void execute(ChangeProductGroupStatusCommand command);
}
