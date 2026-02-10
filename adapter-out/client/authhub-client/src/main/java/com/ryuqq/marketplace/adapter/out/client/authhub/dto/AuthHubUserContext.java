package com.ryuqq.marketplace.adapter.out.client.authhub.dto;

import com.ryuqq.authhub.sdk.model.auth.MyContextResponse;
import java.util.List;

/**
 * AuthHub 사용자 컨텍스트.
 *
 * @param userId 사용자 ID
 * @param email 이메일
 * @param name 사용자 이름
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름
 * @param organizationId 조직 ID
 * @param organizationName 조직 이름
 * @param roles 역할 목록
 * @param permissions 권한 목록
 * @param phoneNumber 핸드폰 번호
 */
public record AuthHubUserContext(
        String userId,
        String email,
        String name,
        String phoneNumber,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName,
        List<RoleInfo> roles,
        List<String> permissions) {

    /** 불변 리스트로 방어적 복사를 수행하는 Compact Canonical Constructor. */
    public AuthHubUserContext {
        roles = roles != null ? List.copyOf(roles) : List.of();
        permissions = permissions != null ? List.copyOf(permissions) : List.of();
    }

    /**
     * 역할 정보.
     *
     * @param id 역할 ID
     * @param name 역할 이름
     */
    public record RoleInfo(String id, String name) {}

    /**
     * MyContextResponse에서 변환합니다.
     *
     * @param response AuthHub SDK 응답
     * @return AuthHubUserContext
     */
    public static AuthHubUserContext from(MyContextResponse response) {
        List<RoleInfo> roles =
                response.roles() != null
                        ? response.roles().stream()
                                .map(r -> new RoleInfo(r.id(), r.name()))
                                .toList()
                        : List.of();

        return new AuthHubUserContext(
                response.userId(),
                response.email(),
                response.name(),
                response.phoneNumber(),
                response.tenant() != null ? response.tenant().id() : null,
                response.tenant() != null ? response.tenant().name() : null,
                response.organization() != null ? response.organization().id() : null,
                response.organization() != null ? response.organization().name() : null,
                roles,
                response.permissions() != null ? response.permissions() : List.of());
    }
}
