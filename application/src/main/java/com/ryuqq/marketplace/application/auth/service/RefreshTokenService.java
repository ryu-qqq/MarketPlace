package com.ryuqq.marketplace.application.auth.service;

import com.ryuqq.marketplace.application.auth.dto.command.RefreshCommand;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import com.ryuqq.marketplace.application.auth.manager.AuthManager;
import com.ryuqq.marketplace.application.auth.port.in.RefreshTokenUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 토큰 갱신 서비스.
 *
 * <p>RefreshTokenUseCase를 구현하며, AuthManager를 통해 토큰 갱신을 수행합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class RefreshTokenService implements RefreshTokenUseCase {

    private final AuthManager authManager;

    public RefreshTokenService(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public RefreshResult execute(RefreshCommand command) {
        return authManager.refresh(command.refreshToken());
    }
}
