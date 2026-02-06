package com.ryuqq.marketplace.application.auth.port.in;

import com.ryuqq.marketplace.application.auth.dto.command.LoginCommand;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;

/**
 * 로그인 UseCase.
 *
 * <p>사용자 인증 후 액세스 토큰을 발급합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface LoginUseCase {

    /**
     * 로그인을 수행합니다.
     *
     * @param command 로그인 Command
     * @return 로그인 결과 (토큰 정보 포함)
     */
    LoginResult execute(LoginCommand command);
}
