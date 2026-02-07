package com.ryuqq.marketplace.application.auth.service;

import com.ryuqq.marketplace.application.auth.dto.command.LogoutCommand;
import com.ryuqq.marketplace.application.auth.manager.AuthManager;
import com.ryuqq.marketplace.application.auth.port.in.LogoutUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 로그아웃 서비스.
 *
 * <p>LogoutUseCase를 구현하며, AuthManager를 통해 로그아웃을 수행합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class LogoutService implements LogoutUseCase {

    private final AuthManager authManager;

    public LogoutService(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void execute(LogoutCommand command) {
        authManager.logout(command.userId());
    }
}
