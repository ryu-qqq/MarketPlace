package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Canonical Name Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>비어있지 않음</li>
 *   <li>최대 255자</li>
 *   <li>정규화 메서드 제공</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CanonicalName(String value) {

    private static final int MAX_LENGTH = 255;

    /**
     * Compact Constructor (검증 로직)
     */
    public CanonicalName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CanonicalName은 null이거나 빈 문자열일 수 없습니다.");
        }

        value = value.trim();

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("CanonicalName은 %d자를 초과할 수 없습니다: %d", MAX_LENGTH, value.length())
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 정규 이름
     * @return CanonicalName
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static CanonicalName of(String value) {
        return new CanonicalName(value);
    }

    /**
     * 정규화된 이름 반환 (소문자, 공백 제거)
     *
     * @return 정규화된 문자열
     */
    public String normalized() {
        return value.toLowerCase()
            .replaceAll("[^a-z0-9]", "");
    }

    /**
     * 정규화된 이름으로 새 CanonicalName 생성
     *
     * @return 정규화된 CanonicalName
     */
    public CanonicalName normalize() {
        return new CanonicalName(normalized());
    }
}
