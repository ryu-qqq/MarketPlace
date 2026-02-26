package com.ryuqq.marketplace.application.commoncode.port.in.command;

import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;

/** 공통 코드 등록 UseCase. */
public interface RegisterCommonCodeUseCase {

    Long execute(RegisterCommonCodeCommand command);
}
