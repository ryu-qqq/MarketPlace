package com.ryuqq.marketplace.application.externalsource.port.in.command;

import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;

/** 외부 소스 등록 UseCase. */
public interface RegisterExternalSourceUseCase {

    Long execute(RegisterExternalSourceCommand command);
}
