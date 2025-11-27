package com.ryuqq.marketplace.application.category.assembler;

import com.ryuqq.marketplace.application.category.dto.response.CategoryPathResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryTreeNode;
import com.ryuqq.marketplace.application.category.dto.response.CategoryTreeResponse;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CategoryAssembler - Domain ↔ Application DTO 변환
 *
 * <p><strong>Zero-Tolerance 규칙</strong>:</p>
 * <ul>
 *   <li>No Lombok</li>
 *   <li>Law of Demeter 준수</li>
 *   <li>Plain Java</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class CategoryAssembler {

    /**
     * Domain → Response DTO 변환
     *
     * @param category Category Aggregate
     * @return CategoryResponse
     */
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.idValue(),
            category.codeValue(),
            category.nameKo(),
            category.nameEn(),
            category.parentIdValue(),
            category.depthValue(),
            category.pathValue(),
            category.sortOrderValue(),
            category.isLeaf(),
            category.status().name(),
            category.isVisible(),
            category.isListable(),
            category.department() != null ? category.department().name() : null,
            category.productGroup() != null ? category.productGroup().name() : null,
            category.genderScope() != null ? category.genderScope().name() : null,
            category.ageGroup() != null ? category.ageGroup().name() : null,
            category.metaDisplayName(),
            category.seoSlug(),
            category.iconUrl()
        );
    }

    /**
     * Domain List → Response List 변환
     *
     * @param categories Category 목록
     * @return CategoryResponse 목록
     */
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Domain List → Tree Response 변환
     *
     * @param categories Category 목록
     * @return CategoryTreeResponse
     */
    public CategoryTreeResponse toTreeResponse(List<Category> categories) {
        if (categories.isEmpty()) {
            return CategoryTreeResponse.empty();
        }

        // Map: categoryId → Category
        Map<Long, Category> categoryMap = categories.stream()
            .collect(Collectors.toMap(Category::idValue, c -> c));

        // Map: categoryId → CategoryTreeNode
        Map<Long, CategoryTreeNode> nodeMap = new HashMap<>();

        // 1. 모든 Category를 CategoryTreeNode로 변환
        for (Category category : categories) {
            CategoryResponse response = toResponse(category);
            nodeMap.put(category.idValue(), new CategoryTreeNode(response));
        }

        // 2. 부모-자식 관계 연결
        List<CategoryTreeNode> roots = new ArrayList<>();
        for (Category category : categories) {
            Long categoryId = category.idValue();
            Long parentId = category.parentIdValue();

            CategoryTreeNode node = nodeMap.get(categoryId);

            if (parentId == null) {
                // 루트 노드
                roots.add(node);
            } else {
                // 자식 노드 → 부모에 추가
                CategoryTreeNode parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.addChild(node);
                } else {
                    // 부모가 없으면 루트로 처리 (데이터 불일치 방어)
                    roots.add(node);
                }
            }
        }

        // 3. 정렬 (sortOrder 기준)
        sortTreeNodes(roots);

        return CategoryTreeResponse.of(roots, categories.size());
    }

    /**
     * Domain List → Path Response 변환 (breadcrumb)
     *
     * @param categoryId 현재 카테고리 ID
     * @param ancestors 조상 카테고리 목록 (루트 → 현재)
     * @return CategoryPathResponse
     */
    public CategoryPathResponse toPathResponse(Long categoryId, List<Category> ancestors) {
        List<CategoryResponse> ancestorResponses = toResponseList(ancestors);
        return CategoryPathResponse.of(categoryId, ancestorResponses);
    }

    // ========== Private Helpers ==========

    /**
     * 트리 노드 재귀 정렬 (sortOrder 기준)
     *
     * @param nodes 정렬할 노드 목록
     */
    private void sortTreeNodes(List<CategoryTreeNode> nodes) {
        if (nodes.isEmpty()) {
            return;
        }

        // 현재 레벨 정렬
        nodes.sort(Comparator.comparingInt(n -> n.category().sortOrder()));

        // 자식 레벨 재귀 정렬
        for (CategoryTreeNode node : nodes) {
            if (node.hasChildren()) {
                sortTreeNodes(new ArrayList<>(node.children()));
            }
        }
    }
}
