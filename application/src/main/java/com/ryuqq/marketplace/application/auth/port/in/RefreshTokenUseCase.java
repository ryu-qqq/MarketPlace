package com.ryuqq.marketplace.application.auth.port.in;

import com.ryuqq.marketplace.application.auth.dto.command.RefreshCommand;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;

/**
 * 토큰 갱신 UseCase.
 *
 * <p>리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface RefreshTokenUseCase {

    /**
     * 토큰 갱신을 수행합니다.
     *
     * @param command 토큰 갱신 Command
     * @return 토큰 갱신 결과
     */
    RefreshResult execute(RefreshCommand command);
}
