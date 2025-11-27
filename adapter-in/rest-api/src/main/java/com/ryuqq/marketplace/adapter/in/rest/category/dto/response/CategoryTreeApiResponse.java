package com.ryuqq.marketplace.adapter.in.rest.category.dto.response;

import java.util.List;

/**
 * 카테고리 트리 전체 응답 DTO
 *
 * <p>카테고리 트리 전체를 반환하는 API 응답 DTO입니다.</p>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record CategoryTreeApiResponse(
    List<CategoryTreeNodeApiResponse> roots,
    int totalCount
) {
    public CategoryTreeApiResponse(List<CategoryTreeNodeApiResponse> roots) {
        this(roots, countNodes(roots));
    }

    private static int countNodes(List<CategoryTreeNodeApiResponse> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return 0;
        }
        int count = nodes.size();
        for (CategoryTreeNodeApiResponse node : nodes) {
            count += countNodes(node.children());
        }
        return count;
    }
}
