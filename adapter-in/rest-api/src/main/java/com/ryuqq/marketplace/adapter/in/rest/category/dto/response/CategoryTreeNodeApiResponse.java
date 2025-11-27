package com.ryuqq.marketplace.adapter.in.rest.category.dto.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 카테고리 트리 노드 응답 DTO
 *
 * <p>트리 구조를 표현하기 위한 노드 DTO입니다.
 * 각 노드는 카테고리 정보와 자식 노드 목록을 가집니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java 클래스</li>
 *   <li>불변성 유지 - children은 방어적 복사로 반환</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public final class CategoryTreeNodeApiResponse {

    private final CategoryApiResponse category;
    private final List<CategoryTreeNodeApiResponse> children;

    public CategoryTreeNodeApiResponse(CategoryApiResponse category) {
        this.category = category;
        this.children = new ArrayList<>();
    }

    /**
     * 자식 노드 추가
     *
     * @param child 자식 노드
     */
    public void addChild(CategoryTreeNodeApiResponse child) {
        children.add(child);
    }

    /**
     * 카테고리 정보 반환
     *
     * @return 카테고리 정보
     */
    public CategoryApiResponse category() {
        return category;
    }

    /**
     * 자식 노드 목록 반환 (불변)
     *
     * @return 자식 노드 목록 (방어적 복사)
     */
    public List<CategoryTreeNodeApiResponse> children() {
        return Collections.unmodifiableList(children);
    }
}
