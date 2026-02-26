package com.ryuqq.marketplace.application.auth.port.in;

import com.ryuqq.marketplace.application.auth.dto.command.LogoutCommand;

/**
 * 로그아웃 UseCase.
 *
 * <p>현재 세션의 토큰을 무효화합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface LogoutUseCase {

    /**
     * 로그아웃을 수행합니다.
     *
     * @param command 로그아웃 Command
     */
    void execute(LogoutCommand command);
}
