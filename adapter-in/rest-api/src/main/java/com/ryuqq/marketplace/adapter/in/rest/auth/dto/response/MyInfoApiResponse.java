package com.ryuqq.marketplace.adapter.in.rest.auth.dto.response;

import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 내 정보 API 응답.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "내 정보 응답")
public record MyInfoApiResponse(
        @Schema(description = "사용자 ID", example = "user-123") String userId,
        @Schema(description = "이메일", example = "admin@example.com") String email,
        @Schema(description = "사용자 이름", example = "관리자") String name,
        @Schema(description = "테넌트 ID", example = "tenant-123") String tenantId,
        @Schema(description = "테넌트 이름", example = "테넌트명") String tenantName,
        @Schema(description = "조직 ID", example = "org-123") String organizationId,
        @Schema(description = "조직 이름", example = "조직명") String organizationName,
        @Schema(description = "역할 목록") List<RoleApiResponse> roles,
        @Schema(description = "권한 목록") List<String> permissions) {

    /** 역할 API 응답. */
    @Schema(description = "역할 정보")
    public record RoleApiResponse(
            @Schema(description = "역할 ID", example = "role-123") String id,
            @Schema(description = "역할 이름", example = "ADMIN") String name) {}

    /**
     * MyInfoResult에서 변환합니다.
     *
     * @param result Application Layer 결과
     * @return API 응답
     */
    public static MyInfoApiResponse from(MyInfoResult result) {
        List<RoleApiResponse> roles =
                result.roles() != null
                        ? result.roles().stream()
                                .map(r -> new RoleApiResponse(r.id(), r.name()))
                                .toList()
                        : List.of();

        return new MyInfoApiResponse(
                result.userId(),
                result.email(),
                result.name(),
                result.tenantId(),
                result.tenantName(),
                result.organizationId(),
                result.organizationName(),
                roles,
                result.permissions());
    }
}
