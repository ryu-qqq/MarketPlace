package com.ryuqq.marketplace.application.legacy.auth.service;

import com.ryuqq.marketplace.application.legacy.auth.dto.command.LegacyLoginCommand;
import com.ryuqq.marketplace.application.legacy.auth.internal.LegacyLoginCoordinator;
import com.ryuqq.marketplace.application.legacy.auth.port.in.LegacyLoginUseCase;
import org.springframework.stereotype.Service;

/** 레거시 로그인 서비스. */
@Service
public class LegacyLoginService implements LegacyLoginUseCase {

    private final LegacyLoginCoordinator legacyLoginCoordinator;

    public LegacyLoginService(LegacyLoginCoordinator legacyLoginCoordinator) {
        this.legacyLoginCoordinator = legacyLoginCoordinator;
    }

    @Override
    public String execute(LegacyLoginCommand command) {
        return legacyLoginCoordinator.login(command.identifier(), command.password());
    }
}
