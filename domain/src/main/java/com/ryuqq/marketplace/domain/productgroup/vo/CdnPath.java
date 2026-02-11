package com.ryuqq.marketplace.domain.productgroup.vo;

/** CDN/S3 저장 경로 Value Object. */
public record CdnPath(String value) {

    private static final int MAX_LENGTH = 500;

    public CdnPath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CDN 경로는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("CDN 경로는 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static CdnPath of(String value) {
        return new CdnPath(value);
    }
}
