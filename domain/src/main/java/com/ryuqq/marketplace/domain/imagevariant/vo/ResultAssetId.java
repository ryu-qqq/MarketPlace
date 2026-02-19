package com.ryuqq.marketplace.domain.imagevariant.vo;

/**
 * FileFlow 변환 결과 에셋 ID Value Object.
 *
 * @param value 에셋 ID
 */
public record ResultAssetId(String value) {

    private static final int MAX_LENGTH = 100;

    public ResultAssetId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("결과 에셋 ID는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("결과 에셋 ID는 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static ResultAssetId of(String value) {
        return new ResultAssetId(value);
    }
}
