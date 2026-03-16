package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 레거시 상품그룹 목록 조회 요청 DTO (Offset 기반).
 *
 * <p>세토프 어드민의 ProductGroupFilter 호환 형식으로 요청을 수신합니다. Adapter-In Mapper에서 내부
 * LegacyProductGroupSearchParams로 변환합니다.
 *
 * <p>API-DTO-002: 조회 네이밍 Search{Bc}ByOffsetApiRequest.
 */
public record LegacySearchProductGroupByOffsetApiRequest(
        @Parameter(description = "조회 시작일 (yyyy-MM-dd HH:mm:ss)")
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                LocalDateTime startDate,
        @Parameter(description = "조회 종료일 (yyyy-MM-dd HH:mm:ss)")
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                LocalDateTime endDate,
        @Parameter(description = "관리유형 (MENUAL, AUTO, SABANG, SEWON)") String managementType,
        @Parameter(description = "카테고리 ID") Long categoryId,
        @Parameter(description = "브랜드 ID") Long brandId,
        @Parameter(description = "판매자 ID") Long sellerId,
        @Parameter(description = "품절 여부 (Y/N)") String soldOutYn,
        @Parameter(description = "노출 여부 (Y/N)") String displayYn,
        @Parameter(
                        description =
                                "검색 유형 (PRODUCT_GROUP_NAME, PRODUCT_GROUP_ID, INSERT_OPERATOR 등)")
                String searchKeyword,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "최소 판매가") Long minSalePrice,
        @Parameter(description = "최대 판매가") Long maxSalePrice,
        @Parameter(description = "최소 할인율") Long minDiscountRate,
        @Parameter(description = "최대 할인율") Long maxDiscountRate,
        @Parameter(description = "페이지 번호 (0부터)") Integer page,
        @Parameter(description = "페이지 크기") Integer size) {

    public int resolvedPage() {
        return page != null ? page : 0;
    }

    public int resolvedSize() {
        return size != null ? size : 20;
    }
}
