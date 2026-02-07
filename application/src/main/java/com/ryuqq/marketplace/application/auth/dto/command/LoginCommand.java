package com.ryuqq.marketplace.application.auth.dto.command;

/**
 * 로그인 Command.
 *
 * @param identifier 사용자 식별자 (이메일 또는 사용자명)
 * @param password 비밀번호
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LoginCommand(String identifier, String password) {

    public LoginCommand {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
    }
}
