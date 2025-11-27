package com.ryuqq.marketplace.domain.category.vo;

/**
 * Category Name Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>한국어 또는 영어 중 하나는 필수</li>
 *   <li>각 언어별 최대 255자</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryName(String ko, String en) {

    private static final int MAX_LENGTH = 255;

    /**
     * Compact Constructor (검증 로직)
     */
    public CategoryName {
        if ((ko == null || ko.isBlank()) && (en == null || en.isBlank())) {
            throw new IllegalArgumentException("한국어 또는 영어 이름 중 하나는 필수입니다.");
        }
        if (ko != null && ko.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("한국어 이름은 " + MAX_LENGTH + "자를 초과할 수 없습니다: " + ko.length());
        }
        if (en != null && en.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("영어 이름은 " + MAX_LENGTH + "자를 초과할 수 없습니다: " + en.length());
        }
    }

    /**
     * 값 기반 생성
     *
     * @param ko 한국어 이름
     * @param en 영어 이름
     * @return CategoryName
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static CategoryName of(String ko, String en) {
        return new CategoryName(ko, en);
    }

    /**
     * 표시용 이름 반환
     *
     * <p>한국어 우선, 없으면 영어</p>
     *
     * @return 표시용 이름
     */
    public String displayName() {
        return (ko != null && !ko.isBlank()) ? ko : en;
    }
}
