package com.ryuqq.marketplace.application.category.port.out.query;

import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CategoryQueryPort - Category 조회 포트
 *
 * <p><strong>구현체</strong>: persistence-mysql 모듈의 CategoryQueryAdapter</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CategoryQueryPort {

    /**
     * ID로 카테고리 조회
     *
     * @param categoryId 카테고리 ID
     * @return Optional<Category>
     */
    Optional<Category> findById(Long categoryId);

    /**
     * 코드로 카테고리 조회
     *
     * @param code 카테고리 코드
     * @return Optional<Category>
     */
    Optional<Category> findByCode(String code);

    /**
     * 부모 ID로 자식 카테고리 조회
     *
     * @param parentId 부모 카테고리 ID (null이면 루트)
     * @return 자식 카테고리 목록
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 활성/노출 카테고리 전체 조회
     *
     * @return ACTIVE + visible 카테고리 목록
     */
    List<Category> findAllActiveVisible();

    /**
     * 전체 카테고리 조회 (Admin용)
     *
     * @return 모든 카테고리 목록
     */
    List<Category> findAll();

    /**
     * 상품 등록 가능한 리프 카테고리 조회
     *
     * @param query 검색 조건
     * @return 리프 카테고리 목록
     */
    List<Category> findListableLeaves(CategorySearchQuery query);

    /**
     * 하위 카테고리 전체 조회 (path 기반)
     *
     * @param categoryId 기준 카테고리 ID
     * @return 모든 하위 카테고리
     */
    List<Category> findDescendants(Long categoryId);

    /**
     * 상위 카테고리 전체 조회 (breadcrumb)
     *
     * @param categoryId 기준 카테고리 ID
     * @return 루트부터 현재까지 조상 목록
     */
    List<Category> findAncestors(Long categoryId);

    /**
     * 키워드 검색
     *
     * @param keyword 검색 키워드
     * @return 검색 결과
     */
    List<Category> search(String keyword);

    /**
     * 특정 시간 이후 변경된 카테고리 조회 (동기화용)
     *
     * @param since 기준 시간
     * @return 변경된 카테고리 목록
     */
    List<Category> findUpdatedSince(LocalDateTime since);

    /**
     * 하위 카테고리 존재 여부 확인
     *
     * @param categoryId 확인할 카테고리 ID
     * @return 하위가 있으면 true
     */
    boolean hasChildren(Long categoryId);
}
