package com.ryuqq.marketplace.domain.notice.vo;

/**
 * 고시정보 카테고리 이름 Value Object.
 *
 * @param nameKo 한국어 이름 (필수)
 * @param nameEn 영어 이름 (nullable)
 */
public record NoticeCategoryName(String nameKo, String nameEn) {

    private static final int MAX_LENGTH = 100;

    public NoticeCategoryName {
        if (nameKo == null || nameKo.isBlank()) {
            throw new IllegalArgumentException("고시정보 카테고리 한국어 이름은 필수입니다");
        }
        nameKo = nameKo.trim();
        if (nameKo.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("고시정보 카테고리 한국어 이름은 %d자 이내여야 합니다", MAX_LENGTH));
        }
        if (nameEn != null) {
            nameEn = nameEn.trim();
            if (nameEn.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        String.format("고시정보 카테고리 영어 이름은 %d자 이내여야 합니다", MAX_LENGTH));
            }
        }
    }

    public static NoticeCategoryName of(String nameKo, String nameEn) {
        return new NoticeCategoryName(nameKo, nameEn);
    }

    public static NoticeCategoryName ofKorean(String nameKo) {
        return new NoticeCategoryName(nameKo, null);
    }
}
