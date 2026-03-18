package com.ryuqq.marketplace.adapter.out.client.legacyauth.adapter;

import com.ryuqq.marketplace.application.legacyauth.port.out.LegacyPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** BCrypt 기반 레거시 비밀번호 인코더 구현체. */
@Component
public class LegacyBCryptPasswordEncoder implements LegacyPasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
