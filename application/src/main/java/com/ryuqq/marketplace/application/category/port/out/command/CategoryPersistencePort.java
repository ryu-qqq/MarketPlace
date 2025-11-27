package com.ryuqq.marketplace.application.category.port.out.command;

import com.ryuqq.marketplace.domain.category.aggregate.Category;

import java.util.List;

/**
 * CategoryPersistencePort - Category 저장 포트
 *
 * <p><strong>구현체</strong>: persistence-mysql 모듈의 CategoryCommandAdapter</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CategoryPersistencePort {

    /**
     * 카테고리 저장 (생성/수정)
     *
     * @param category 저장할 카테고리
     * @return 저장된 카테고리
     */
    Category persist(Category category);

    /**
     * 여러 카테고리 일괄 저장
     *
     * @param categories 저장할 카테고리 목록
     */
    void persistAll(List<Category> categories);

    /**
     * 카테고리 삭제 (Soft Delete - status 변경)
     *
     * @param categoryId 삭제할 카테고리 ID
     */
    void delete(Long categoryId);

    /**
     * 코드 중복 확인
     *
     * @param code 확인할 코드
     * @return 존재하면 true
     */
    boolean existsByCode(String code);
}
