package com.ryuqq.marketplace.adapter.in.rest.brand.dto.query;

import jakarta.validation.constraints.Min;

/**
 * 브랜드 검색 요청 DTO
 *
 * <p>브랜드를 검색하고 필터링할 때 사용하는 Query DTO입니다.</p>
 * <p>모든 필터 조건은 Optional이며, 제공된 조건만 적용됩니다.</p>
 *
 * <ul>
 *   <li>keyword: 검색 키워드 (브랜드명, 코드 등에서 검색, 선택)</li>
 *   <li>status: 브랜드 상태 필터 (선택, 예: ACTIVE, INACTIVE)</li>
 *   <li>department: 브랜드 부문 필터 (선택)</li>
 *   <li>isLuxury: 럭셔리 브랜드 여부 필터 (선택)</li>
 *   <li>page: 페이지 번호 (0부터 시작, 기본값: 0)</li>
 *   <li>size: 페이지 크기 (기본값: 20)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record BrandSearchApiRequest(
    String keyword,
    String status,
    String department,
    Boolean isLuxury,
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    Integer page,
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    Integer size
) {
    /**
     * Compact Constructor - 기본값 설정
     *
     * <p>page가 null이면 0으로, size가 null이면 20으로 초기화합니다.</p>
     */
    public BrandSearchApiRequest {
        if (page == null) page = 0;
        if (size == null) size = 20;
    }
}
