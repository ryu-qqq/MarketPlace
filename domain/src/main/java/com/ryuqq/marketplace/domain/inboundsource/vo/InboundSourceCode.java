package com.ryuqq.marketplace.domain.inboundsource.vo;

/** 인바운드 소스 고유 코드. */
public record InboundSourceCode(String value) {

    private static final int MAX_LENGTH = 100;

    public InboundSourceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("인바운드 소스 코드는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("인바운드 소스 코드는 %d자를 초과할 수 없습니다", MAX_LENGTH));
        }
    }

    public static InboundSourceCode of(String value) {
        return new InboundSourceCode(value);
    }
}
