package com.ryuqq.marketplace.domain.category.vo;

/**
 * 카테고리 이름 정보 Value Object.
 *
 * @param nameKo 한글 카테고리명 (필수)
 * @param nameEn 영문 카테고리명 (nullable)
 */
public record CategoryName(String nameKo, String nameEn) {

    private static final int MAX_LENGTH = 255;

    public CategoryName {
        if (nameKo == null || nameKo.isBlank()) {
            throw new IllegalArgumentException("한글 카테고리명은 필수입니다");
        }
        nameKo = nameKo.trim();
        if (nameKo.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("한글 카테고리명은 %d자 이내여야 합니다", MAX_LENGTH));
        }
        if (nameEn != null) {
            nameEn = nameEn.trim();
            if (nameEn.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        String.format("영문 카테고리명은 %d자 이내여야 합니다", MAX_LENGTH));
            }
        }
    }

    public static CategoryName of(String nameKo, String nameEn) {
        return new CategoryName(nameKo, nameEn);
    }

    public static CategoryName ofKorean(String nameKo) {
        return new CategoryName(nameKo, null);
    }
}
