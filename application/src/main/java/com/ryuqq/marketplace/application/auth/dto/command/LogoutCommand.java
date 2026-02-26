package com.ryuqq.marketplace.application.auth.dto.command;

/**
 * 로그아웃 Command.
 *
 * @param userId 사용자 ID
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LogoutCommand(String userId) {

    public LogoutCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
    }
}
