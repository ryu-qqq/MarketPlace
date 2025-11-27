package com.ryuqq.marketplace.domain.brand.vo;

/**
 * Alias Name Value Object
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>original: 원문 (필수)</li>
 *   <li>normalized: 자동 정규화 (소문자, 특수문자/공백 제거)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record AliasName(String original, String normalized) {

    private static final int MAX_LENGTH = 255;

    /**
     * Compact Constructor (검증 로직 + 자동 정규화)
     */
    public AliasName {
        if (original == null || original.isBlank()) {
            throw new IllegalArgumentException("original은 null이거나 빈 문자열일 수 없습니다.");
        }

        original = original.trim();

        if (original.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("original은 %d자를 초과할 수 없습니다: %d", MAX_LENGTH, original.length())
            );
        }

        // normalized가 null이면 자동 계산
        if (normalized == null || normalized.isBlank()) {
            normalized = normalize(original);
        }
    }

    /**
     * 원문 기반 생성 (normalized 자동 계산)
     *
     * @param original 원문
     * @return AliasName
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static AliasName of(String original) {
        return new AliasName(original, null);
    }

    /**
     * 원문과 정규화 문자열로 생성
     *
     * @param original 원문
     * @param normalized 정규화된 문자열
     * @return AliasName
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static AliasName of(String original, String normalized) {
        return new AliasName(original, normalized);
    }

    /**
     * 문자열 정규화 (소문자, 특수문자/공백 제거)
     *
     * @param value 원본 문자열
     * @return 정규화된 문자열
     */
    private static String normalize(String value) {
        return value.toLowerCase()
            .replaceAll("[^a-z0-9]", "");
    }

    /**
     * 정규화 문자열로 검색 매칭
     *
     * @param searchTerm 검색어
     * @return normalized가 검색어를 포함하면 true
     */
    public boolean matches(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return false;
        }
        String normalizedSearch = normalize(searchTerm);
        return normalized.contains(normalizedSearch);
    }
}
