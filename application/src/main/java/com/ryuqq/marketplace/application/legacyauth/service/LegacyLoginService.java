package com.ryuqq.marketplace.application.legacyauth.service;

import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.manager.AuthManager;
import com.ryuqq.marketplace.application.legacyauth.dto.command.LegacyLoginCommand;
import com.ryuqq.marketplace.application.legacyauth.port.in.LegacyLoginUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** 레거시 로그인 서비스. */
@Service
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class LegacyLoginService implements LegacyLoginUseCase {

    private final AuthManager authManager;

    public LegacyLoginService(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public String execute(LegacyLoginCommand command) {
        LoginResult result = authManager.login(command.identifier(), command.password());
        return result.accessToken();
    }
}
