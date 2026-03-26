package com.ryuqq.marketplace.application.legacy.auth.port.out;

/** 레거시 비밀번호 인코더 포트. */
public interface LegacyPasswordEncoder {

    /**
     * 평문 비밀번호와 인코딩된 비밀번호가 일치하는지 검증합니다.
     *
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword 인코딩된 비밀번호 (BCrypt 등)
     * @return 일치하면 true
     */
    boolean matches(String rawPassword, String encodedPassword);
}
