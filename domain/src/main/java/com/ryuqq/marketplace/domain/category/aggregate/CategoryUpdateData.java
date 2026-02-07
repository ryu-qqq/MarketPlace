package com.ryuqq.marketplace.domain.category.aggregate;

import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;

/**
 * 카테고리 수정 데이터.
 *
 * @param categoryName 카테고리 이름
 * @param sortOrder 정렬 순서
 * @param status 상태
 * @param department 상품 부문
 * @param categoryGroup 카테고리 그룹 (고시정보 연결용)
 */
public record CategoryUpdateData(
        CategoryName categoryName,
        SortOrder sortOrder,
        CategoryStatus status,
        Department department,
        CategoryGroup categoryGroup) {}
