package com.ryuqq.marketplace.domain.category.vo;

import java.util.Arrays;
import java.util.List;

/**
 * Category Path Value Object (계층 경로)
 *
 * <p><strong>도메인 규칙</strong>:</p>
 * <ul>
 *   <li>슬래시(/)로 구분된 카테고리 ID 경로</li>
 *   <li>예: "1/10/100" (대 카테고리/중 카테고리/소 카테고리)</li>
 *   <li>최대 1000자</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CategoryPath(String value) {

    private static final String SEPARATOR = "/";
    private static final int MAX_LENGTH = 1000;

    /**
     * Compact Constructor (검증 로직)
     */
    public CategoryPath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryPath는 null이거나 빈 문자열일 수 없습니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("CategoryPath는 " + MAX_LENGTH + "자를 초과할 수 없습니다: " + value.length());
        }
    }

    /**
     * 문자열로부터 경로 생성
     *
     * @param path 경로 문자열
     * @return CategoryPath
     */
    public static CategoryPath of(String path) {
        return new CategoryPath(path);
    }

    /**
     * Root 경로 생성
     *
     * @param categoryId 카테고리 ID
     * @return CategoryPath
     */
    public static CategoryPath root(Long categoryId) {
        return new CategoryPath(String.valueOf(categoryId));
    }

    /**
     * 하위 카테고리 추가
     *
     * @param childId 자식 카테고리 ID
     * @return 새로운 CategoryPath
     */
    public CategoryPath appendChild(Long childId) {
        return new CategoryPath(value + SEPARATOR + childId);
    }

    /**
     * 경로를 ID 리스트로 변환
     *
     * @return ID 리스트
     */
    public List<Long> toIdList() {
        return Arrays.stream(value.split(SEPARATOR))
                .map(Long::parseLong)
                .toList();
    }

    /**
     * 경로 깊이 계산
     *
     * @return 깊이 (0부터 시작)
     */
    public int depth() {
        return (int) value.chars().filter(c -> c == '/').count();
    }

    /**
     * 다른 경로의 하위인지 확인
     *
     * @param other 비교 대상 경로
     * @return 하위 경로이면 true
     */
    public boolean isDescendantOf(CategoryPath other) {
        return value.startsWith(other.value + SEPARATOR);
    }
}
