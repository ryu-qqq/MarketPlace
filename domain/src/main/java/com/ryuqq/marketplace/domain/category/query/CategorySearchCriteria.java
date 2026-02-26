package com.ryuqq.marketplace.domain.category.query;

import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.List;

/**
 * Category 검색 조건 Criteria.
 *
 * <p>카테고리 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param parentId 부모 카테고리 ID 필터 (null이면 전체)
 * @param depth 계층 깊이 필터 (null이면 전체)
 * @param leaf 리프 노드 여부 필터 (null이면 전체)
 * @param statuses 카테고리 상태 필터 (empty이면 전체)
 * @param departments 부문 필터 (empty이면 전체)
 * @param categoryGroups 카테고리 그룹 필터 (empty이면 전체, 고시정보 연결용)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record CategorySearchCriteria(
        Long parentId,
        Integer depth,
        Boolean leaf,
        List<CategoryStatus> statuses,
        List<Department> departments,
        List<CategoryGroup> categoryGroups,
        CategorySearchField searchField,
        String searchWord,
        QueryContext<CategorySortKey> queryContext) {

    public CategorySearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        departments = departments != null ? List.copyOf(departments) : List.of();
        categoryGroups = categoryGroups != null ? List.copyOf(categoryGroups) : List.of();
    }

    public static CategorySearchCriteria of(
            Long parentId,
            Integer depth,
            Boolean leaf,
            List<CategoryStatus> statuses,
            List<Department> departments,
            List<CategoryGroup> categoryGroups,
            CategorySearchField searchField,
            String searchWord,
            QueryContext<CategorySortKey> queryContext) {
        return new CategorySearchCriteria(
                parentId,
                depth,
                leaf,
                statuses,
                departments,
                categoryGroups,
                searchField,
                searchWord,
                queryContext);
    }

    public static CategorySearchCriteria defaultCriteria() {
        return new CategorySearchCriteria(
                null,
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                QueryContext.defaultOf(CategorySortKey.defaultKey()));
    }

    public static CategorySearchCriteria activeOnly() {
        return new CategorySearchCriteria(
                null,
                null,
                null,
                List.of(CategoryStatus.ACTIVE),
                List.of(),
                List.of(),
                null,
                null,
                QueryContext.defaultOf(CategorySortKey.defaultKey()));
    }

    public static CategorySearchCriteria byParent(Long parentId) {
        return new CategorySearchCriteria(
                parentId,
                null,
                null,
                List.of(CategoryStatus.ACTIVE),
                List.of(),
                List.of(),
                null,
                null,
                QueryContext.defaultOf(CategorySortKey.defaultKey()));
    }

    public static CategorySearchCriteria byCategoryGroup(CategoryGroup categoryGroup) {
        return new CategorySearchCriteria(
                null,
                null,
                null,
                List.of(CategoryStatus.ACTIVE),
                List.of(),
                List.of(categoryGroup),
                null,
                null,
                QueryContext.defaultOf(CategorySortKey.defaultKey()));
    }

    /** 부모 카테고리 필터가 있는지 확인. */
    public boolean hasParentFilter() {
        return parentId != null;
    }

    /** 깊이 필터가 있는지 확인. */
    public boolean hasDepthFilter() {
        return depth != null;
    }

    /** 리프 노드 필터가 있는지 확인. */
    public boolean hasLeafFilter() {
        return leaf != null;
    }

    /** 상태 필터가 있는지 확인. */
    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    /** 부문 필터가 있는지 확인. */
    public boolean hasDepartmentFilter() {
        return !departments.isEmpty();
    }

    /** 카테고리 그룹 필터가 있는지 확인. */
    public boolean hasCategoryGroupFilter() {
        return !categoryGroups.isEmpty();
    }

    /** 검색 조건이 있는지 확인. */
    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 특정 필드 검색인지 확인. */
    public boolean hasSearchField() {
        return searchField != null;
    }

    /** 페이지 크기 반환 (편의 메서드). */
    public int size() {
        return queryContext.size();
    }

    /** 오프셋 반환 (편의 메서드). */
    public long offset() {
        return queryContext.offset();
    }

    /** 현재 페이지 번호 반환 (편의 메서드). */
    public int page() {
        return queryContext.page();
    }
}
