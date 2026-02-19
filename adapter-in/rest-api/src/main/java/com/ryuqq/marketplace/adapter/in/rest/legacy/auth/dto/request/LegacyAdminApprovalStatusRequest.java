package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request;

import java.util.List;

/** 세토프 AdministratorsApprovalStatusRequestDto 호환 요청 DTO. */
public record LegacyAdminApprovalStatusRequest(List<Long> adminIds, String approvalStatus) {

    public LegacyAdminApprovalStatusRequest {
        adminIds = adminIds == null ? List.of() : List.copyOf(adminIds);
    }
}
