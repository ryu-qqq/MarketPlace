package com.ryuqq.marketplace.application.productgroup.port.in.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;

/** 상품 그룹 배치 상태 변경 UseCase. */
public interface BatchChangeProductGroupStatusUseCase {

    void execute(BatchChangeProductGroupStatusCommand command);
}
