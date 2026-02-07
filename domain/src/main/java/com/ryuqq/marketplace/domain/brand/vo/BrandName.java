package com.ryuqq.marketplace.domain.brand.vo;

/**
 * 브랜드 이름 정보 Value Object.
 *
 * <p>한글명, 영문명, 약칭을 묶어서 관리합니다.
 *
 * @param nameKo 한글 브랜드명 (nullable)
 * @param nameEn 영문 브랜드명 (nullable)
 * @param shortName 약칭 (nullable)
 */
public record BrandName(String nameKo, String nameEn, String shortName) {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_SHORT_NAME_LENGTH = 100;

    public BrandName {
        if (nameKo != null) {
            nameKo = nameKo.trim();
            if (nameKo.length() > MAX_NAME_LENGTH) {
                throw new IllegalArgumentException(
                        String.format("한글 브랜드명은 %d자 이내여야 합니다", MAX_NAME_LENGTH));
            }
        }
        if (nameEn != null) {
            nameEn = nameEn.trim();
            if (nameEn.length() > MAX_NAME_LENGTH) {
                throw new IllegalArgumentException(
                        String.format("영문 브랜드명은 %d자 이내여야 합니다", MAX_NAME_LENGTH));
            }
        }
        if (shortName != null) {
            shortName = shortName.trim();
            if (shortName.length() > MAX_SHORT_NAME_LENGTH) {
                throw new IllegalArgumentException(
                        String.format("브랜드 약칭은 %d자 이내여야 합니다", MAX_SHORT_NAME_LENGTH));
            }
        }
    }

    public static BrandName of(String nameKo, String nameEn, String shortName) {
        return new BrandName(nameKo, nameEn, shortName);
    }

    public static BrandName empty() {
        return new BrandName(null, null, null);
    }
}
