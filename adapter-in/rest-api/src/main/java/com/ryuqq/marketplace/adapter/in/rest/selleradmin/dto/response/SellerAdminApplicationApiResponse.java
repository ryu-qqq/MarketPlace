package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response;

import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * SellerAdminApplicationApiResponse - 셀러 관리자 가입 신청 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * @param sellerAdminId 셀러 관리자 ID
 * @param sellerId 셀러 ID
 * @param loginId 로그인 ID
 * @param name 관리자 이름
 * @param phoneNumber 휴대폰 번호
 * @param status 상태
 * @param authUserId 인증 서버 사용자 ID (승인 후)
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 가입 신청 응답 DTO")
public record SellerAdminApplicationApiResponse(
        @Schema(description = "셀러 관리자 ID", example = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f6g")
                String sellerAdminId,
        @Schema(description = "셀러 ID", example = "100") Long sellerId,
        @Schema(description = "로그인 ID", example = "admin@example.com") String loginId,
        @Schema(description = "관리자 이름", example = "홍길동") String name,
        @Schema(description = "휴대폰 번호", example = "010-1234-5678") String phoneNumber,
        @Schema(description = "상태", example = "PENDING_APPROVAL") String status,
        @Schema(description = "인증 서버 사용자 ID (승인 후)", example = "auth-user-uuid-123")
                String authUserId,
        @Schema(description = "생성일시", example = "2026-02-04T10:00:00Z") Instant createdAt,
        @Schema(description = "수정일시", example = "2026-02-04T10:00:00Z") Instant updatedAt) {

    /**
     * Result를 ApiResponse로 변환합니다.
     *
     * @param result Application Result
     * @return API Response
     */
    public static SellerAdminApplicationApiResponse from(SellerAdminApplicationResult result) {
        return new SellerAdminApplicationApiResponse(
                result.sellerAdminId(),
                result.sellerId(),
                result.loginId(),
                result.name(),
                result.phoneNumber(),
                result.status(),
                result.authUserId(),
                result.createdAt(),
                result.updatedAt());
    }
}
