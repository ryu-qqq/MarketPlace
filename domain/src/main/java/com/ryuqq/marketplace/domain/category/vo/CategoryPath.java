package com.ryuqq.marketplace.domain.category.vo;

/**
 * 카테고리 경로 (Path Enumeration) Value Object.
 *
 * <p>조상 카테고리 ID를 '/'로 연결한 문자열입니다. (예: "1/2/3")
 *
 * @param value 경로 문자열
 */
public record CategoryPath(String value) {

    private static final int MAX_LENGTH = 1000;

    public CategoryPath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("카테고리 경로는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("카테고리 경로는 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static CategoryPath of(String value) {
        return new CategoryPath(value);
    }

    /**
     * 자식 경로 생성.
     *
     * @param childId 자식 카테고리 ID
     * @return 새로운 경로
     */
    public CategoryPath appendChild(Long childId) {
        return new CategoryPath(value + "/" + childId);
    }

    /** 루트 카테고리인지 확인. */
    public boolean isRoot() {
        return !value.contains("/");
    }

    /** 경로에 포함된 카테고리 수 반환. */
    public int depth() {
        return (int) value.chars().filter(ch -> ch == '/').count();
    }
}
