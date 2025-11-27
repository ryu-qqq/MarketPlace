package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Brand Name Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>ko/en 중 최소 하나 필수</li>
 *   <li>shortName은 선택</li>
 *   <li>각 최대 255자</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record BrandName(
    String nameKo,
    String nameEn,
    String shortName
) {

    private static final int MAX_LENGTH = 255;

    /**
     * Compact Constructor (검증 로직)
     */
    public BrandName {
        if ((nameKo == null || nameKo.isBlank()) && (nameEn == null || nameEn.isBlank())) {
            throw new IllegalArgumentException("nameKo 또는 nameEn 중 최소 하나는 필수입니다.");
        }

        nameKo = nameKo != null ? nameKo.trim() : null;
        nameEn = nameEn != null ? nameEn.trim() : null;
        shortName = shortName != null ? shortName.trim() : null;

        if (nameKo != null && nameKo.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("nameKo는 %d자를 초과할 수 없습니다: %d", MAX_LENGTH, nameKo.length())
            );
        }

        if (nameEn != null && nameEn.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("nameEn은 %d자를 초과할 수 없습니다: %d", MAX_LENGTH, nameEn.length())
            );
        }

        if (shortName != null && shortName.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("shortName은 %d자를 초과할 수 없습니다: %d", MAX_LENGTH, shortName.length())
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param nameKo 한글명 (선택)
     * @param nameEn 영문명 (선택)
     * @param shortName 단축명 (선택)
     * @return BrandName
     * @throws IllegalArgumentException nameKo, nameEn 모두 없는 경우
     */
    public static BrandName of(String nameKo, String nameEn, String shortName) {
        return new BrandName(nameKo, nameEn, shortName);
    }

    /**
     * 한글명만으로 생성
     *
     * @param nameKo 한글명
     * @return BrandName
     */
    public static BrandName ofKorean(String nameKo) {
        return new BrandName(nameKo, null, null);
    }

    /**
     * 영문명만으로 생성
     *
     * @param nameEn 영문명
     * @return BrandName
     */
    public static BrandName ofEnglish(String nameEn) {
        return new BrandName(null, nameEn, null);
    }

    /**
     * 표시용 이름 반환 (우선순위: ko > en)
     *
     * @return 표시용 이름
     */
    public String displayName() {
        if (nameKo != null && !nameKo.isBlank()) {
            return nameKo;
        }
        return nameEn;
    }
}
