package com.ryuqq.marketplace.domain.saleschannel.vo;

/** 판매채널명 Value Object. */
public record ChannelName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public ChannelName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("판매채널명은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("판매채널명은 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static ChannelName of(String value) {
        return new ChannelName(value);
    }
}
