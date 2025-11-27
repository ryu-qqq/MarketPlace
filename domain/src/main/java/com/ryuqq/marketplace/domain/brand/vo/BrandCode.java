package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Brand Code Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>패턴: 대문자로 시작, A-Z/0-9/_ 조합</li>
 *   <li>길이: 2-100자</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record BrandCode(String value) {

    private static final String CODE_PATTERN = "^[A-Z][A-Z0-9_]{1,99}$";
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    /**
     * Compact Constructor (검증 로직)
     */
    public BrandCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BrandCode는 null이거나 빈 문자열일 수 없습니다.");
        }

        value = value.trim();

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("BrandCode는 %d-%d자여야 합니다: %d", MIN_LENGTH, MAX_LENGTH, value.length())
            );
        }

        if (!value.matches(CODE_PATTERN)) {
            throw new IllegalArgumentException(
                "BrandCode는 대문자로 시작하고 A-Z/0-9/_ 조합이어야 합니다: " + value
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 브랜드 코드
     * @return BrandCode
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static BrandCode of(String value) {
        return new BrandCode(value);
    }
}
