package com.ryuqq.marketplace.domain.channeloptionmapping.vo;

/** 외부몰 옵션 코드 Value Object. 채널별 옵션 식별 코드. */
public record ExternalOptionCode(String value) {

    public ExternalOptionCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("외부 옵션 코드는 비어있을 수 없습니다");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("외부 옵션 코드는 100자를 초과할 수 없습니다");
        }
    }

    public static ExternalOptionCode of(String value) {
        return new ExternalOptionCode(value);
    }
}
