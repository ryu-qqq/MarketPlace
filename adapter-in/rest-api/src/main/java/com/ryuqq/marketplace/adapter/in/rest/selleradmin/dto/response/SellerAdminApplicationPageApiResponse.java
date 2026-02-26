package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response;

import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationPageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * SellerAdminApplicationPageApiResponse - 셀러 관리자 가입 신청 목록 응답 DTO (페이징).
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * @param content 신청 목록
 * @param page 현재 페이지 (0-based)
 * @param size 페이지 크기
 * @param totalElements 전체 개수
 * @param totalPages 전체 페이지 수
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 가입 신청 목록 응답 DTO (페이징)")
public record SellerAdminApplicationPageApiResponse(
        @Schema(description = "신청 목록") List<SellerAdminApplicationApiResponse> content,
        @Schema(description = "현재 페이지 (0-based)", example = "0") int page,
        @Schema(description = "페이지 크기", example = "20") int size,
        @Schema(description = "전체 개수", example = "100") long totalElements,
        @Schema(description = "전체 페이지 수", example = "5") int totalPages) {

    /**
     * Result를 ApiResponse로 변환합니다.
     *
     * @param result Application Result
     * @return API Response
     */
    public static SellerAdminApplicationPageApiResponse from(
            SellerAdminApplicationPageResult result) {
        List<SellerAdminApplicationApiResponse> content =
                result.content().stream().map(SellerAdminApplicationApiResponse::from).toList();

        return new SellerAdminApplicationPageApiResponse(
                content,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements(),
                result.pageMeta().totalPages());
    }
}
