package com.ryuqq.marketplace.application.commoncodetype.port.in.command;

import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;

/** 공통 코드 타입 등록 UseCase. */
public interface RegisterCommonCodeTypeUseCase {

    Long execute(RegisterCommonCodeTypeCommand command);
}
