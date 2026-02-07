package com.ryuqq.marketplace.application.auth.dto.command;

/**
 * 토큰 갱신 Command.
 *
 * @param refreshToken 리프레시 토큰
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RefreshCommand(String refreshToken) {

    public RefreshCommand {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refreshToken must not be blank");
        }
    }
}
