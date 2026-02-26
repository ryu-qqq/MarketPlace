package com.ryuqq.marketplace.domain.canonicaloption.vo;

/**
 * 캐노니컬 옵션 값 이름 Value Object.
 *
 * @param nameKo 한국어 이름 (필수)
 * @param nameEn 영어 이름 (nullable)
 */
public record CanonicalOptionValueName(String nameKo, String nameEn) {

    private static final int MAX_LENGTH = 100;

    public CanonicalOptionValueName {
        if (nameKo == null || nameKo.isBlank()) {
            throw new IllegalArgumentException("캐노니컬 옵션 값 한국어 이름은 필수입니다");
        }
        nameKo = nameKo.trim();
        if (nameKo.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("캐노니컬 옵션 값 한국어 이름은 %d자 이내여야 합니다", MAX_LENGTH));
        }
        if (nameEn != null) {
            nameEn = nameEn.trim();
            if (nameEn.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        String.format("캐노니컬 옵션 값 영어 이름은 %d자 이내여야 합니다", MAX_LENGTH));
            }
        }
    }

    public static CanonicalOptionValueName of(String nameKo, String nameEn) {
        return new CanonicalOptionValueName(nameKo, nameEn);
    }
}
