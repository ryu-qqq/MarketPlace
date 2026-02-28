package com.ryuqq.marketplace.domain.inboundsource.vo;

import java.util.Locale;

/**
 * 시스템에 등록된 알려진 인바운드 소스 코드.
 *
 * <p>하드코딩 방지를 위해 알려진 소스 코드를 enum으로 관리합니다.
 */
public enum KnownInboundSourceCode {
    MUSTIT("MUSTIT"),
    SETOF("SETOF");

    private final String code;

    KnownInboundSourceCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    /** 주어진 sourceCode가 이 enum 값과 일치하는지 확인한다. */
    public boolean matches(String sourceCode) {
        return this.code.equals(sourceCode);
    }

    /**
     * 문자열로부터 KnownInboundSourceCode 변환.
     *
     * @throws IllegalArgumentException 알 수 없는 소스 코드
     */
    public static KnownInboundSourceCode fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("소스 코드는 필수입니다");
        }
        try {
            return KnownInboundSourceCode.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("알 수 없는 인바운드 소스 코드: " + value);
        }
    }
}
