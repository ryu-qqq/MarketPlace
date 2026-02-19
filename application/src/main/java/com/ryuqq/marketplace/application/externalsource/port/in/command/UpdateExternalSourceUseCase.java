package com.ryuqq.marketplace.application.externalsource.port.in.command;

import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;

/** 외부 소스 수정 UseCase. */
public interface UpdateExternalSourceUseCase {

    void execute(UpdateExternalSourceCommand command);
}
