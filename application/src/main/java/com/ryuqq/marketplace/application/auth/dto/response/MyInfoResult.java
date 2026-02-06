package com.ryuqq.marketplace.application.auth.dto.response;

import java.util.List;

/**
 * 내 정보 조회 결과.
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
 * @author ryu-qqq
 * @since 1.0.0
 */
public record MyInfoResult(
        String userId,
        String email,
        String name,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName,
        List<RoleInfo> roles,
        List<String> permissions) {

    /**
     * 역할 정보.
     *
     * @param id 역할 ID
     * @param name 역할 이름
     */
    public record RoleInfo(String id, String name) {}
}
