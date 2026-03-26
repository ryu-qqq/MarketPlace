package com.ryuqq.marketplace.application.legacy.auth.port.in;

import com.ryuqq.marketplace.application.legacy.auth.dto.command.LegacyLoginCommand;

/** 레거시 로그인 UseCase. */
public interface LegacyLoginUseCase {

    /**
     * 레거시 로그인을 수행합니다.
     *
     * @param command 레거시 로그인 Command
     * @return 액세스 토큰
     */
    String execute(LegacyLoginCommand command);
}
