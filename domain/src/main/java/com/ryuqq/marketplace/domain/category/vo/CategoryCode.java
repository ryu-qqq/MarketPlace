package com.ryuqq.marketplace.domain.category.vo;

import java.util.regex.Pattern;

/**
 * Category Code Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>대문자 알파벳으로 시작</li>
 *   <li>대문자, 숫자, 언더스코어만 허용</li>
 *   <li>최대 100자</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryCode(String value) {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{0,99}$");

    /**
     * Compact Constructor (검증 로직)
     */
    public CategoryCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryCode는 null이거나 빈 문자열일 수 없습니다.");
        }
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "CategoryCode는 대문자로 시작하고 대문자, 숫자, 언더스코어만 포함해야 합니다: " + value
            );
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 카테고리 코드
     * @return CategoryCode
     * @throws IllegalArgumentException 코드 형식이 잘못된 경우
     */
    public static CategoryCode of(String value) {
        return new CategoryCode(value);
    }
}
