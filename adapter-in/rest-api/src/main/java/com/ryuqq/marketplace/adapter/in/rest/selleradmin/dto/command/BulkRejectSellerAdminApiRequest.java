package com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * BulkRejectSellerAdminApiRequest - 셀러 관리자 가입 신청 일괄 거절 요청 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-002: Jakarta Validation 필수.
 *
 * @param sellerAdminIds 거절할 셀러 관리자 ID 목록
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "셀러 관리자 가입 신청 일괄 거절 요청 DTO")
public record BulkRejectSellerAdminApiRequest(
        @Schema(description = "거절할 셀러 관리자 ID 목록")
                @NotEmpty(message = "셀러 관리자 ID 목록은 필수입니다.")
                @Size(max = 100, message = "한 번에 최대 100건까지 처리 가능합니다.")
                List<String> sellerAdminIds) {}
